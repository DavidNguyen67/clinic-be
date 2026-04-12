package com.camel.clinic.service;

import com.camel.clinic.entity.BaseEntity;
import com.camel.clinic.util.MapperUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
public class BaseService<T extends BaseEntity, R extends JpaRepository<T, UUID> & JpaSpecificationExecutor<T>> {

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

            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            log.error("Error creating entity: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create entity", "message", e.getMessage()));
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
            String sortBy = (String) queryParams.getOrDefault("sortBy", "id");
            String sortDir = (String) queryParams.getOrDefault("sortDir", "asc");

            Sort sort = sortDir.equalsIgnoreCase("desc")
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();

            Pageable pageable = PageRequest.of(page, size, sort);
            Page<T> resultPage = repository.findAll(pageable);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("data", resultPage.getContent());
            response.put("page", resultPage.getNumber());
            response.put("size", resultPage.getSize());
            response.put("totalItems", resultPage.getTotalElements());
            response.put("totalPages", resultPage.getTotalPages());

            log.info("Listed {} entities (page={}, size={})", resultPage.getNumberOfElements(), page, size);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error listing entities: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to list entities", "message", e.getMessage()));
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
                    .orElseThrow(() -> new EntityNotFoundException("Entity not found with id: " + id));

            Object result = filterFields(entity, fields);
            return ResponseEntity.ok(result);
        } catch (EntityNotFoundException e) {
            log.warn("Entity not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error retrieving entity id={}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve entity", "message", e.getMessage()));
        }
    }

    // ==================== UPDATE ====================

    /**
     * Cập nhật bản ghi theo id (partial update — chỉ ghi đè field không null)
     */
    public ResponseEntity<?> update(String id, T data, String fields, Map<String, Object> params) {
        try {
            T existing = repository.findById(UUID.fromString(id))
                    .orElseThrow(() -> new EntityNotFoundException("Entity not found with id: " + id));

            MapperUtils.convertModelToEntity(data, existing);

            T saved = repository.save(existing);
            log.info("Updated entity id={}", id);

            Object result = filterFields(saved, fields);
            return ResponseEntity.ok(result);
        } catch (EntityNotFoundException e) {
            log.warn("Entity not found for update: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error updating entity id={}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update entity", "message", e.getMessage()));
        }
    }

    // ==================== DELETE ====================

    /**
     * Xóa bản ghi theo id
     */
    public ResponseEntity<?> delete(String id) {
        try {
            if (!repository.existsById(UUID.fromString(id))) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Entity not found with id: " + id));
            }
            repository.deleteById(UUID.fromString(id));
            log.info("Deleted entity id={}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting entity id={}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete entity", "message", e.getMessage()));
        }
    }

    // ==================== GET BY IDS ====================

    /**
     * Lấy nhiều bản ghi theo danh sách id
     */
    public ResponseEntity<?> getByIds(List<UUID> ids, String fields) {
        try {
            List<T> entities = repository.findAllById(ids);

            List<Object> result = new ArrayList<>();
            for (T entity : entities) {
                result.add(filterFields(entity, fields));
            }

            log.info("Retrieved {} entities by ids", result.size());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error fetching entities by ids: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get entities by ids", "message", e.getMessage()));
        }
    }

    // ==================== HELPERS ====================

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

    private int parseIntParam(Map<String, Object> params, String key, int defaultValue) {
        Object val = params.get(key);
        if (val == null) return defaultValue;
        try {
            return Integer.parseInt(val.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}