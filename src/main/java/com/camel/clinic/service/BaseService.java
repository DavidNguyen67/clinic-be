package com.camel.clinic.service;

import com.camel.clinic.dto.ApiPaged;
import com.camel.clinic.entity.SoftDeletableEntity;
import com.camel.clinic.exception.NotFoundException;
import com.camel.clinic.util.MapperUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional(rollbackFor = Exception.class)
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
    @Transactional(rollbackFor = Exception.class)
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
    @Transactional(rollbackFor = Exception.class)
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
    @Transactional(rollbackFor = Exception.class)
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

    protected Specification<T> fieldEquals(String fieldName, Object value) {
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

    protected Specification<T> nestedFieldEqual(String join, String fieldName, Object value) {
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

    // Trong BaseService hoặc 1 class SpecificationUtils riêng
    protected <T, V> Specification<T> fieldIn(String fieldName, Object raw, Class<V> type) {
        if (raw == null) return Specification.unrestricted();

        // Single value
        if (type.isInstance(raw)) {
            V value = type.cast(raw);
            return (root, query, cb) -> cb.equal(root.get(fieldName), value);
        }

        // List value
        if (raw instanceof List<?> list && !list.isEmpty()) {
            List<V> values = list.stream()
                    .filter(type::isInstance)
                    .map(type::cast)
                    .toList();
            if (values.isEmpty()) return Specification.unrestricted();
            return (root, query, cb) -> root.get(fieldName).in(values);
        }

        return Specification.unrestricted();
    }

    protected <T> Specification<T> fieldOnDate(String fieldName, Date date) {
        if (date == null) return Specification.unrestricted();
        return (root, query, cb) -> {
            Calendar cal = Calendar.getInstance();

            cal.setTime(date);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            Date startOfDay = cal.getTime();

            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);
            Date endOfDay = cal.getTime();

            return cb.between(root.get(fieldName), startOfDay, endOfDay);
        };
    }

    protected Specification<T> fieldBetweenDates(String fieldName, Date from, Date to) {
        return (root, query, cb) -> {
            if (from == null && to == null) return cb.conjunction();
            if (from == null) return cb.lessThanOrEqualTo(root.get(fieldName), to);
            if (to == null) return cb.greaterThanOrEqualTo(root.get(fieldName), from);
            return cb.between(root.get(fieldName), from, to);
        };
    }

    protected Specification<T> excludeId(Object value) {
        if (value == null) return Specification.unrestricted();
        UUID excludeUuid = value instanceof UUID u ? u : CommonService.parseUuid(value);
        return (root, query, cb) -> cb.notEqual(root.get("id"), excludeUuid);
    }

    protected Specification<T> keywordSpec(String keyword, String[][] fields) {
        if (keyword == null || keyword.isBlank()) return Specification.unrestricted();
        if (fields == null || fields.length == 0) return Specification.unrestricted();

        String pattern = "%" + keyword.toLowerCase().trim() + "%";

        return (root, query, cb) -> {
            Predicate[] predicates = Arrays.stream(fields)
                    .map(f -> switch (f.length) {
                        case 1 -> // direct: {"fullName"}
                                cb.like(cb.lower(root.get(f[0])), pattern);
                        case 2 -> // nested 1 level: {"specialty", "name"}
                                cb.like(cb.lower(root.join(f[0], JoinType.LEFT).get(f[1])), pattern);
                        case 3 -> // nested 2 level: {"doctorProfile", "user", "fullName"}
                                cb.like(cb.lower(root.join(f[0], JoinType.LEFT).join(f[1], JoinType.LEFT).get(f[2])), pattern);
                        default -> cb.conjunction();
                    })
                    .toArray(Predicate[]::new);
            return cb.or(predicates);
        };
    }
}