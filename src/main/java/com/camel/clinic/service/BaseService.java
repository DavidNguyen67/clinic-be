package com.camel.clinic.service;

import com.camel.clinic.dto.ApiPaged;
import com.camel.clinic.entity.SoftDeletableEntity;
import com.camel.clinic.exception.NotFoundException;
import com.camel.clinic.util.MapperUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.persistence.criteria.JoinType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
public abstract class BaseService<T extends SoftDeletableEntity, R extends JpaRepository<T, UUID> & JpaSpecificationExecutor<T>> {

    protected final Supplier<T> entityFactory;
    protected final ObjectMapper objectMapper;
    protected final R repository;

    public BaseService(Supplier<T> entityFactory, R repository) {
        this.entityFactory = entityFactory;
        this.repository = repository;
        this.objectMapper = new ObjectMapper()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }


    /**
     * Tạo mới một bản ghi
     */
    public ResponseEntity<?> create(T data) {
        try {
            T initObject = entityFactory.get();
            MapperUtils.convertModelToEntity(data, initObject);

            T saved = repository.save(initObject);
            log.info("Created entity: {}", saved);

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(saved.getId())
                    .toUri();

            return ResponseEntity.created(location)
                    .body(saved);
        } catch (Exception e) {
            log.error("Error creating entity: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create entity: " + e.getMessage(), e);
        }
    }

    // ==================== LIST ====================

    /**
     * Lấy danh sách bản ghi có hỗ trợ phân trang và sắp xếp
     * <p>
     * queryParams hỗ trợ:
     * - page     : số trang (default 0)
     * - size     : số bản ghi mỗi trang (default 20)
     * - sortBy   : tên field để sort (default "id")
     * - sortDir  : "asc" hoặc "desc" (default "asc")
     */
    public ResponseEntity<?> list(Map<String, Object> queryParams) {
        try {
            int page = parseIntParam(queryParams, "page", 0);
            int size = parseIntParam(queryParams, "size", 20);
            String sortBy = (String) queryParams.getOrDefault("sortBy", "createdAt");
            String sortDir = (String) queryParams.getOrDefault("sortDir", "asc");

            Sort sort = sortDir.equalsIgnoreCase("desc")
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();

            Pageable pageable = PageRequest.of(page, size, sort);

            // Subclass có thể override buildSpec() để thêm filter riêng
            Specification<T> spec = buildSpec(queryParams);

            Page<T> resultPage = repository.findAll(spec, pageable);

            ApiPaged<T> paged = ApiPaged.of(
                    resultPage.getContent(),
                    resultPage.getTotalElements(),
                    resultPage.getNumber(),
                    resultPage.getSize(),
                    resultPage.getTotalPages()
            );

            log.info("Listed {} entities (page={}, size={})", resultPage.getNumberOfElements(), page, size);
            return ResponseEntity.ok(paged);
        } catch (Exception e) {
            log.error("Error listing entities: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to list entities: " + e.getMessage(), e);
        }
    }

    /**
     * Đếm tổng số bản ghi
     *
     * @return tổng số bản ghi
     */
    public ResponseEntity<?> count() {
        try {
            Specification<T> notDeleted = (root, query, cb) ->
                    cb.isNull(root.get("deletedAt"));

            long total = repository.count(notDeleted);
            log.info("Counted {} entities", total);
            return ResponseEntity.ok(total);
        } catch (Exception e) {
            log.error("Error counting entities: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to count entities: " + e.getMessage(), e);
        }
    }

    // ==================== RETRIEVE ====================

    /**
     * Lấy bản ghi theo id
     *
     * @param fields comma-separated tên các field muốn trả về, null = trả hết
     */
    public ResponseEntity<?> retrieve(String id, String fields) {
        try {
            T entity = repository.findById(UUID.fromString(id))
                    .orElseThrow(() -> new NotFoundException("Entity not found with id: " + id));

            Object result = filterFields(entity, fields);
            return ResponseEntity.ok(result);
        } catch (NotFoundException e) {
            log.warn("Entity not found: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error retrieving entity id={}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve entity: " + e.getMessage(), e);
        }
    }

    // ==================== UPDATE ====================

    /**
     * Cập nhật bản ghi theo id (partial update — chỉ ghi đè field không null)
     */
    public ResponseEntity<?> update(String id, T data, String fields) {
        try {
            T existing = repository.findById(UUID.fromString(id))
                    .orElseThrow(() -> new NotFoundException("Entity not found with id: " + id));

            // convertModelToEntity  → reset rồi copy  → KHÔNG dùng cho partial update
            // mergeDataSourceToTarget → chỉ copy non-null → ĐÚNG cho partial update
            MapperUtils.mergeDataSourceToTarget(data, existing);

            T saved = repository.save(existing);
            log.info("Updated entity id={}", id);

            Object result = filterFields(saved, fields);
            return ResponseEntity.ok(result);
        } catch (NotFoundException e) {
            log.warn("Entity not found for update: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error updating entity id={}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to update entity: " + e.getMessage(), e);
        }
    }

    // ==================== DELETE ====================

    /**
     * Xóa bản ghi theo id
     */
    public ResponseEntity<?> delete(String id) {
        try {
            if (!repository.existsById(UUID.fromString(id))) {
                log.warn("Entity not found for delete: id={}", id);
                throw new NotFoundException("Entity not found with id: " + id);
            }
            T entityToDelete = repository.findById(UUID.fromString(id)).orElseThrow();
            entityToDelete.setDeletedAt(new Date());
            repository.save(entityToDelete);
            //            repository.deleteById(UUID.fromString(id));
            log.info("Deleted entity id={}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting entity id={}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to delete entity: " + e.getMessage(), e);
        }
    }

    // ==================== RESTORE ====================

    /**
     * Khôi phục bản ghi đã bị soft-delete
     */
    public ResponseEntity<?> restore(String id) {
        try {
            Specification<T> spec = (root, query, cb) -> cb.and(
                    cb.equal(root.get("id"), UUID.fromString(id)),
                    cb.isNotNull(root.get("deletedAt"))
            );

            T entity = repository.findOne(spec)
                    .orElseThrow(() -> new NotFoundException("Deleted entity not found with id: " + id));

            entity.setDeletedAt(null);
            T saved = repository.save(entity);

            log.info("Restored entity id={}", id);
            return ResponseEntity.ok(saved);
        } catch (NotFoundException e) {
            log.warn("Entity not found for restore: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error restoring entity id={}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to restore entity: " + e.getMessage(), e);
        }
    }

    // ==================== GET BY IDS ====================

    /**
     * Lấy nhiều bản ghi theo danh sách id
     */
    public ResponseEntity<?> getByIds(List<UUID> ids, String fields) {
        try {
            Specification<T> spec = (root, query, cb) -> cb.and(
                    root.get("id").in(ids),
                    cb.isNull(root.get("deletedAt"))
            );

            List<T> entities = repository.findAll(spec);

            List<Object> result = new ArrayList<>();
            for (T entity : entities) {
                result.add(filterFields(entity, fields));
            }

            log.info("Retrieved {} entities by ids", result.size());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error fetching entities by ids: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get entities by ids: " + e.getMessage(), e);
        }
    }

    // ==================== HELPERS ====================

    protected Specification<T> hasField(String fieldName, Object value) {
        if (value == null) return null;
        if (value instanceof String s && s.isBlank()) return null;
        return (root, query, cb) -> cb.equal(root.get(fieldName), value);
    }

    protected Specification<T> fieldLike(String fieldName, String keyword) {
        if (keyword == null || keyword.isBlank()) return null;
        return (root, query, cb) ->
                cb.like(cb.lower(root.get(fieldName)), "%" + keyword.toLowerCase() + "%");
    }

    /**
     * Lọc fields của entity trước khi trả về response.
     * Nếu fields == null hoặc rỗng thì trả về toàn bộ entity.
     */
    @SuppressWarnings("unchecked")
    protected Object filterFields(T entity, String fields) throws JsonProcessingException {
        if (fields == null || fields.isBlank()) {
            return entity;
        }

        Set<String> allowedFields = Arrays.stream(fields.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());

        // Convert entity → Map rồi chỉ giữ lại các key được yêu cầu
        Map<String, Object> fullMap = objectMapper.convertValue(entity, Map.class);
        Map<String, Object> filtered = new LinkedHashMap<>();
        for (String f : allowedFields) {
            if (fullMap.containsKey(f)) {
                filtered.put(f, fullMap.get(f));
            }
        }
        return filtered;
    }


    protected Specification<T> buildSpec(Map<String, Object> queryParams) {
        return buildBaseSpec(queryParams); // default: chỉ filter notDeleted + status
    }

    protected Specification<T> buildBaseSpec(Map<String, Object> queryParams) {
        return Specification.<T>unrestricted()
                .and(notDeleted());
    }

    protected Specification<T> hasNestedField(String join, String fieldName, Object value) {
        if (value == null) return null;
        if (value instanceof String s && s.isBlank()) return null;
        return (root, query, cb) -> cb.equal(root.join(join, JoinType.LEFT).get(fieldName), value);
    }

    protected Specification<T> nestedFieldLike(String join, String fieldName, String keyword) {
        if (keyword == null || keyword.isBlank()) return null;
        return (root, query, cb) ->
                cb.like(cb.lower(root.join(join, JoinType.LEFT).get(fieldName)), "%" + keyword.toLowerCase() + "%");
    }

    protected Specification<T> notDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    protected int parseIntParam(Map<String, Object> params, String key, int defaultValue) {
        Object val = params.get(key);
        if (val == null) return defaultValue;
        try {
            return Integer.parseInt(val.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}