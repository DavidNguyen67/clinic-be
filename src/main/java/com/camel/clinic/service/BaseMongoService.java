package com.camel.clinic.service;

import com.camel.clinic.document.SofDeleteDocument;
import com.camel.clinic.dto.ApiPaged;
import com.camel.clinic.exception.NotFoundException;
import com.camel.clinic.util.MapperUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public abstract class BaseMongoService<T extends SofDeleteDocument, R extends MongoRepository<T, String>> {

    protected final Supplier<T> documentFactory;
    protected final ObjectMapper objectMapper;
    protected final R repository;
    protected final MongoTemplate mongoTemplate;
    protected final Class<T> documentClass;

    protected BaseMongoService(Supplier<T> documentFactory, R repository,
                               MongoTemplate mongoTemplate, Class<T> documentClass) {
        this.documentFactory = documentFactory;
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
        this.documentClass = documentClass;
        this.objectMapper = new ObjectMapper()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    // ─────────────────────────────────────────────
    // CREATE
    // ─────────────────────────────────────────────

    @Transactional
    public ResponseEntity<?> create(T data) {
        try {
            T init = documentFactory.get();
            MapperUtils.convertModelToEntity(data, init);

            T saved = repository.save(init);
            log.info("Created document: {}", saved.getId());

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(saved.getId())
                    .toUri();

            return ResponseEntity.created(location).body(saved);
        } catch (Exception e) {
            log.error("Error creating document: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create document: " + e.getMessage(), e);
        }
    }

    // ─────────────────────────────────────────────
    // BULK CREATE
    // ─────────────────────────────────────────────

    @Transactional
    public ResponseEntity<?> bulkCreate(List<T> dataList) {
        try {
            if (dataList == null || dataList.isEmpty()) {
                return ResponseEntity.badRequest().body("Request body must not be empty");
            }

            List<T> toSave = dataList.stream()
                    .map(data -> {
                        T init = documentFactory.get();
                        MapperUtils.convertModelToEntity(data, init);
                        return init;
                    })
                    .toList();

            List<T> saved = repository.saveAll(toSave);
            log.info("Bulk created {} documents", saved.size());

            return ResponseEntity.status(HttpStatus.MULTI_STATUS).body(saved);
        } catch (Exception e) {
            log.error("Error bulk creating documents: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to bulk create documents: " + e.getMessage(), e);
        }
    }

    // ─────────────────────────────────────────────
    // LIST (pagination + filter)
    // ─────────────────────────────────────────────

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

            // Subclass override buildCriteria() để add filter riêng
            Criteria criteria = buildCriteria(queryParams);
            Query query = new Query(criteria).with(sort);

            long total = mongoTemplate.count(query, documentClass);

            query.with(pageable);
            List<T> content = mongoTemplate.find(query, documentClass);

            Page<T> resultPage = new PageImpl<>(content, pageable, total);

            ApiPaged<T> paged = ApiPaged.of(
                    resultPage.getContent(),
                    resultPage.getTotalElements(),
                    resultPage.getNumber(),
                    resultPage.getSize(),
                    resultPage.getTotalPages()
            );

            log.info("Listed {} documents (page={}, size={})", content.size(), page, size);
            return ResponseEntity.ok(paged);
        } catch (Exception e) {
            log.error("Error listing documents: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to list documents: " + e.getMessage(), e);
        }
    }

    // ─────────────────────────────────────────────
    // COUNT
    // ─────────────────────────────────────────────

    public ResponseEntity<?> count() {
        try {
            Query query = new Query(notDeleted());
            long total = mongoTemplate.count(query, documentClass);
            log.info("Counted {} documents", total);
            return ResponseEntity.ok(total);
        } catch (Exception e) {
            log.error("Error counting documents: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to count documents: " + e.getMessage(), e);
        }
    }

    // ─────────────────────────────────────────────
    // RETRIEVE
    // ─────────────────────────────────────────────

    public ResponseEntity<?> retrieve(String id, String fields) {
        try {
            T entity = repository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Document not found with id: " + id));

            Object result = filterFields(entity, fields);
            return ResponseEntity.ok(result);
        } catch (NotFoundException e) {
            log.warn("Document not found: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error retrieving document id={}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve document: " + e.getMessage(), e);
        }
    }

    // ─────────────────────────────────────────────
    // UPDATE (partial — chỉ ghi đè field không null)
    // ─────────────────────────────────────────────

    @Transactional
    public ResponseEntity<?> update(String id, T data, String fields) {
        try {
            T existing = repository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Document not found with id: " + id));

            MapperUtils.mergeDataSourceToTarget(data, existing);

            T saved = repository.save(existing);
            log.info("Updated document id={}", id);

            Object result = filterFields(saved, fields);
            return ResponseEntity.ok(result);
        } catch (NotFoundException e) {
            log.warn("Document not found for update: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error updating document id={}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to update document: " + e.getMessage(), e);
        }
    }

    // ─────────────────────────────────────────────
    // DELETE (soft)
    // ─────────────────────────────────────────────

    @Transactional
    public ResponseEntity<?> delete(String id) {
        try {
            T entity = repository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Document not found with id: " + id));

            entity.setDeletedAt(new Date());
            repository.save(entity);

            log.info("Soft-deleted document id={}", id);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            log.warn("Document not found for delete: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error deleting document id={}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to delete document: " + e.getMessage(), e);
        }
    }

    @Transactional
    public ResponseEntity<?> restore(String id) {
        try {
            Query query = new Query(Criteria.where("_id").is(id)
                    .and("deleted_at").ne(null));

            T entity = mongoTemplate.findOne(query, documentClass);
            if (entity == null) {
                throw new NotFoundException("Deleted document not found with id: " + id);
            }

            entity.setDeletedAt(null);
            T saved = repository.save(entity);

            log.info("Restored document id={}", id);
            return ResponseEntity.ok(saved);
        } catch (NotFoundException e) {
            log.warn("Document not found for restore: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error restoring document id={}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to restore document: " + e.getMessage(), e);
        }
    }

    // ─────────────────────────────────────────────
    // GET BY IDs
    // ─────────────────────────────────────────────

    public ResponseEntity<?> getByIds(List<String> ids, String fields) {
        try {
            Query query = new Query(Criteria.where("_id").in(ids)
                    .and("deleted_at").isNull());

            List<T> entities = mongoTemplate.find(query, documentClass);

            List<Object> result = entities.stream()
                    .map(e -> filterFields(e, fields))
                    .toList();

            log.info("Retrieved {} documents by ids", result.size());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error fetching documents by ids: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get documents by ids: " + e.getMessage(), e);
        }
    }

    // ─────────────────────────────────────────────
    // CRITERIA HELPERS  (multiField pattern — tương đương Specification JPA)
    //
    // Mỗi helper nhận varargs String... fields:
    //   - 1 phần tử  → direct field:          "status"
    //   - 2 phần tử  → nested 1 cấp:          "address", "city"   → document.address.city
    //   - 3 phần tử  → nested 2 cấp:          "order", "item", "name"
    //
    // Kết quả các field được nối bằng OR (giống multiField* JPA).
    // Dùng andAll() để AND các criteria lại.
    // ─────────────────────────────────────────────

    /**
     * Base: chỉ lọc notDeleted. Subclass override để thêm filter.
     */
    protected Criteria buildCriteria(Map<String, Object> queryParams) {
        return notDeleted();
    }

    /**
     * deletedAt == null
     */
    protected Criteria notDeleted() {
        return Criteria.where("deleted_at").isNull();
    }

    // ── resolve nested field path: ["a","b","c"] → "a.b.c" ──────────────────
    private String resolvePath(String[] f) {
        return String.join(".", f);
    }

    // ── guard: bỏ qua field path rỗng ────────────────────────────────────────
    private boolean isValidField(String[] f) {
        return f != null && f.length >= 1 && f.length <= 3;
    }

    /**
     * OR equals trên nhiều field.
     * <pre>multiFieldEquals("ACTIVE", new String[]{"status"}, new String[]{"profile","status"})</pre>
     */
    protected Criteria multiFieldEquals(Object value, String[]... fields) {
        if (value == null) return new Criteria();
        if (value instanceof String s && s.isBlank()) return new Criteria();

        List<Criteria> list = Arrays.stream(fields)
                .filter(this::isValidField)
                .map(f -> Criteria.where(resolvePath(f)).is(value))
                .toList();

        return orList(list);
    }

    /**
     * OR LIKE %keyword% (case-insensitive) trên nhiều field.
     * <pre>multiFieldLike("john", new String[]{"fullName"}, new String[]{"profile","email"})</pre>
     */
    protected Criteria multiFieldLike(String keyword, String[]... fields) {
        if (keyword == null || keyword.isBlank()) return new Criteria();
        Pattern pattern = Pattern.compile(keyword.trim(), Pattern.CASE_INSENSITIVE);

        List<Criteria> list = Arrays.stream(fields)
                .filter(this::isValidField)
                .map(f -> Criteria.where(resolvePath(f)).regex(pattern))
                .toList();

        return orList(list);
    }

    /**
     * OR IN list trên nhiều field.
     * <pre>multiFieldIn(statusList, new String[]{"status"}, new String[]{"order","status"})</pre>
     */
    protected <V> Criteria multiFieldIn(List<V> values, String[]... fields) {
        if (values == null || values.isEmpty()) return new Criteria();

        List<Criteria> list = Arrays.stream(fields)
                .filter(this::isValidField)
                .map(f -> Criteria.where(resolvePath(f)).in(values))
                .toList();

        return orList(list);
    }

    /**
     * AND NOT IN list trên nhiều field (giống JPA — tất cả field đều phải thỏa mãn).
     * <pre>multiFieldNotIn(excludeIds, new String[]{"id"})</pre>
     */
    protected <V> Criteria multiFieldNotIn(List<V> values, String[]... fields) {
        if (values == null || values.isEmpty()) return new Criteria();

        List<Criteria> list = Arrays.stream(fields)
                .filter(this::isValidField)
                .map(f -> Criteria.where(resolvePath(f)).nin(values))
                .toList();

        // NOT IN → AND (tất cả field đều phải không nằm trong list)
        return andList(list);
    }

    /**
     * OR field nằm trong khoảng một ngày (00:00:00 → 23:59:59).
     * <pre>multiFieldOnDate(date, new String[]{"createdAt"}, new String[]{"order","date"})</pre>
     */
    protected Criteria multiFieldOnDate(Date date, String[]... fields) {
        if (date == null) return new Criteria();
        Date start = atStartOfDay(date);
        Date end = atEndOfDay(date);

        List<Criteria> list = Arrays.stream(fields)
                .filter(this::isValidField)
                .map(f -> Criteria.where(resolvePath(f)).gte(start).lte(end))
                .toList();

        return orList(list);
    }

    /**
     * OR BETWEEN from–to trên nhiều field.
     * <pre>multiFieldBetweenDates(from, to, new String[]{"createdAt"}, new String[]{"updatedAt"})</pre>
     */
    protected Criteria multiFieldBetweenDates(Date from, Date to, String[]... fields) {
        if (from == null && to == null) return new Criteria();
        Date normalizedFrom = from != null ? atStartOfDay(from) : null;
        Date normalizedTo = to != null ? atEndOfDay(to) : null;

        List<Criteria> list = Arrays.stream(fields)
                .filter(this::isValidField)
                .map(f -> {
                    String path = resolvePath(f);
                    if (normalizedFrom == null) return Criteria.where(path).lte(normalizedTo);
                    if (normalizedTo == null) return Criteria.where(path).gte(normalizedFrom);
                    return Criteria.where(path).gte(normalizedFrom).lte(normalizedTo);
                })
                .toList();

        return orList(list);
    }

    /**
     * OR &gt; / &gt;= value trên nhiều field.
     * <pre>multiFieldGreaterThan(minPrice, true, new String[]{"price"})</pre>
     */
    protected <V extends Comparable<? super V>> Criteria multiFieldGreaterThan(V value, boolean orEqual, String[]... fields) {
        if (value == null) return new Criteria();

        List<Criteria> list = Arrays.stream(fields)
                .filter(this::isValidField)
                .map(f -> {
                    String path = resolvePath(f);
                    return orEqual
                            ? Criteria.where(path).gte(value)
                            : Criteria.where(path).gt(value);
                })
                .toList();

        return orList(list);
    }

    /**
     * OR &lt; / &lt;= value trên nhiều field.
     * <pre>multiFieldLessThan(maxPrice, true, new String[]{"price"})</pre>
     */
    protected <V extends Comparable<? super V>> Criteria multiFieldLessThan(V value, boolean orEqual, String[]... fields) {
        if (value == null) return new Criteria();

        List<Criteria> list = Arrays.stream(fields)
                .filter(this::isValidField)
                .map(f -> {
                    String path = resolvePath(f);
                    return orEqual
                            ? Criteria.where(path).lte(value)
                            : Criteria.where(path).lt(value);
                })
                .toList();

        return orList(list);
    }

    /**
     * Kết hợp nhiều Criteria bằng AND, tự động bỏ qua Criteria rỗng.
     *
     * <pre>
     * return andAll(
     *     notDeleted(),
     *     multiFieldEquals(status, new String[]{"status"}),
     *     multiFieldLike(keyword, new String[]{"name"}, new String[]{"email"})
     * );
     * </pre>
     */
    protected Criteria andAll(Criteria... criteriaArray) {
        List<Criteria> valid = Arrays.stream(criteriaArray)
                .filter(Objects::nonNull)
                .filter(c -> !c.getCriteriaObject().isEmpty())
                .toList();

        if (valid.isEmpty()) return new Criteria();
        if (valid.size() == 1) return valid.get(0);
        return new Criteria().andOperator(valid.toArray(new Criteria[0]));
    }

    // ── internal helpers ─────────────────────────────────────────────────────

    private Criteria orList(List<Criteria> list) {
        if (list.isEmpty()) return new Criteria();
        if (list.size() == 1) return list.get(0);
        return new Criteria().orOperator(list.toArray(new Criteria[0]));
    }

    private Criteria andList(List<Criteria> list) {
        if (list.isEmpty()) return new Criteria();
        if (list.size() == 1) return list.get(0);
        return new Criteria().andOperator(list.toArray(new Criteria[0]));
    }

    // ─────────────────────────────────────────────
    // UTILS
    // ─────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    protected Object filterFields(T entity, String fields) {
        if (fields == null || fields.isBlank()) return entity;

        Set<String> allowed = Arrays.stream(fields.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());

        Map<String, Object> full = objectMapper.convertValue(entity, Map.class);
        Map<String, Object> filtered = new LinkedHashMap<>();
        allowed.forEach(f -> {
            if (full.containsKey(f)) filtered.put(f, full.get(f));
        });
        return filtered;
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

    protected <E extends Enum<E>> List<E> parseEnumList(Object raw, Class<E> enumClass) {
        if (raw == null) return List.of();
        if (raw instanceof String s) {
            if (s.isBlank()) return List.of();
            try {
                return List.of(Enum.valueOf(enumClass, s.toUpperCase().trim()));
            } catch (IllegalArgumentException e) {
                return List.of();
            }
        }
        if (raw instanceof List<?> list) {
            return list.stream()
                    .filter(String.class::isInstance)
                    .map(v -> {
                        try {
                            return Enum.valueOf(enumClass, ((String) v).toUpperCase().trim());
                        } catch (IllegalArgumentException e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .toList();
        }
        return List.of();
    }

    private Date atStartOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private Date atEndOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }
}