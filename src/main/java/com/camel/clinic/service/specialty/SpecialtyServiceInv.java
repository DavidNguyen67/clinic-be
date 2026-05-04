package com.camel.clinic.service.specialty;

import com.camel.clinic.dto.ApiPaged;
import com.camel.clinic.dto.specialty.SpecialtyWithDoctorCountDto;
import com.camel.clinic.entity.DoctorProfile;
import com.camel.clinic.entity.Specialty;
import com.camel.clinic.repository.SpecialtyRepository;
import com.camel.clinic.service.BaseService;
import com.camel.clinic.service.CommonService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SpecialtyServiceInv extends BaseService<Specialty, SpecialtyRepository> {
    private final EntityManager entityManager;

    public SpecialtyServiceInv(SpecialtyRepository repository, EntityManager entityManager) {
        super(Specialty::new, repository);
        this.entityManager = entityManager;
    }

    @Override
    protected Specification<Specialty> buildSpec(Map<String, Object> queryParams) {
        return Specification.<Specialty>unrestricted()
                .and(notDeleted())
                .and(fieldLike("slug", (String) queryParams.get("slug")))
                .and(fieldEquals("isActive", CommonService.parseBoolean(queryParams.get("isActive"))));
    }

    public ResponseEntity<?> listWithDoctorCount(Map<String, Object> queryParams) {
        int page = parseIntParam(queryParams, "page", 0);
        int size = parseIntParam(queryParams, "size", 20);
        String sortBy = (String) queryParams.getOrDefault("sortBy", "doctorCount");
        String sortDir = (String) queryParams.getOrDefault("sortDir", "desc");
        String keyword = (String) queryParams.getOrDefault("keyword", null);

        Specification<Specialty> spec = buildSpec(queryParams);

        // Thêm keyword search vào spec
        if (keyword != null && !keyword.isBlank()) {
            String pattern = "%" + keyword.trim().toLowerCase() + "%";
            Specification<Specialty> keywordSpec = (root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("name")), pattern),
                    cb.like(cb.lower(root.get("slug")), pattern),
                    cb.like(cb.lower(root.get("specialtyType").as(String.class)), pattern)
            );
            spec = spec == null ? keywordSpec : spec.and(keywordSpec);
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // ── Main query ──────────────────────────────────────────────
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<Specialty> root = query.from(Specialty.class);

        Join<Specialty, DoctorProfile> doctorJoin = root.join("doctorProfiles", JoinType.LEFT);
        doctorJoin.on(cb.isNull(doctorJoin.get("deletedAt")));

        Predicate predicate = spec.toPredicate(root, query, cb);
        if (predicate != null) query.where(predicate);

        Expression<Long> countExpr = cb.count(doctorJoin.get("id"));
        query.multiselect(root, countExpr);
        query.groupBy(root);

        // Dynamic sort
        boolean desc = sortDir.equalsIgnoreCase("desc");
        if ("doctorCount".equals(sortBy)) {
            query.orderBy(desc ? cb.desc(countExpr) : cb.asc(countExpr));
        } else {
            try {
                Expression<?> fieldExpr = root.get(sortBy);
                query.orderBy(desc ? cb.desc(fieldExpr) : cb.asc(fieldExpr));
            } catch (IllegalArgumentException e) {
                query.orderBy(cb.desc(countExpr));
            }
        }

        List<Tuple> tuples = entityManager.createQuery(query)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();

        // ── Count query ─────────────────────────────────────────────
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Specialty> countRoot = countQuery.from(Specialty.class);
        Predicate countPredicate = spec.toPredicate(countRoot, countQuery, cb);
        if (countPredicate != null) countQuery.where(countPredicate);
        countQuery.select(cb.count(countRoot));
        long total = entityManager.createQuery(countQuery).getSingleResult();

        // ── Map result ───────────────────────────────────────────────
        List<SpecialtyWithDoctorCountDto> content = tuples.stream()
                .map(t -> {
                    Specialty s = t.get(0, Specialty.class);
                    long doctorCount = t.get(1, Long.class);
                    return new SpecialtyWithDoctorCountDto(
                            s.getId(),
                            s.getName(),
                            s.getSlug(),
                            s.getDescription(),
                            s.getImage(),
                            s.getSpecialtyType(),
                            doctorCount
                    );
                })
                .toList();

        ApiPaged<SpecialtyWithDoctorCountDto> paged = ApiPaged.of(
                content,
                total,
                page,
                size,
                (int) Math.ceil((double) total / size)
        );

        return ResponseEntity.ok(paged);
    }
}