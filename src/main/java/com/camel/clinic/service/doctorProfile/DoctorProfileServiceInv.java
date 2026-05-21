package com.camel.clinic.service.doctorProfile;

import com.camel.clinic.dto.ApiPaged;
import com.camel.clinic.dto.doctorProfile.ResponseDoctorProfileDto;
import com.camel.clinic.entity.DoctorProfile;
import com.camel.clinic.entity.Specialty;
import com.camel.clinic.repository.DoctorProfileRepository;
import com.camel.clinic.service.BaseService;
import com.camel.clinic.service.CommonService;
import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.JoinType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class DoctorProfileServiceInv extends BaseService<DoctorProfile, DoctorProfileRepository> {

    public DoctorProfileServiceInv(DoctorProfileRepository repository) {
        super(DoctorProfile::new, repository);
    }

    public List<DoctorProfile> findInIds(List<String> ids) {
        return repository.findAll(buildSpec(Map.of("ids", ids)));
    }

    @Override
    protected Specification<DoctorProfile> buildSpec(Map<String, Object> queryParams) {
        List<String> ids = (List<String>) queryParams.get("ids");
        List<UUID> uuids = CommonService.parseToList(ids, UUID::fromString);

        return Specification.<DoctorProfile>unrestricted()
                .and(notDeleted())
                .and(multiFieldIn(uuids, new String[]{"id"}))
                .and(multiFieldGreaterThan(CommonService.parseToLong(queryParams.get("minFee")), true,
                        new String[]{"consultationFee"}))
                .and(multiFieldLessThan(CommonService.parseToLong(queryParams.get("maxFee")), true,
                        new String[]{"consultationFee"}))
                .and((root, query, cb) -> {
                    assert query != null;
                    if (!query.getResultType().equals(Long.class)) {
                        root.fetch("user", JoinType.LEFT);
                        Fetch<DoctorProfile, Specialty> specialtyFetch = root.fetch("specialty", JoinType.LEFT);
                        specialtyFetch.fetch("services", JoinType.LEFT);
                        query.distinct(true);
                    }
                    return cb.conjunction();
                })
                .and(fieldEquals("isFeatured", CommonService.parseBoolean(queryParams.get("isFeatured"))))
                .and(nestedFieldLike("user", "fullName", (String) queryParams.get("fullName")))
                .and(nestedFieldLike("specialty", "name", (String) queryParams.get("specialtyName")))
                .and(nestedFieldEqual("specialty", "id", CommonService.parseToUuid(queryParams.get("specialtyId"))));
    }

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

            Specification<DoctorProfile> spec = buildSpec(queryParams);

            Page<DoctorProfile> resultPage = repository.findAll(spec, pageable);

            List<?> content = resultPage.getContent()
                    .stream()
                    .map(ResponseDoctorProfileDto::of)
                    .toList();

            ApiPaged<?> paged = ApiPaged.of(
                    content,
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

}