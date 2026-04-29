package com.camel.clinic.service.specialty;

import com.camel.clinic.dto.ApiPaged;
import com.camel.clinic.entity.Specialty;
import com.camel.clinic.repository.SpecialtyRepository;
import com.camel.clinic.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class SpecialtyServiceInv extends BaseService<Specialty, SpecialtyRepository> {
    public SpecialtyServiceInv(SpecialtyRepository repository) {
        super(Specialty::new, repository);
    }

    @Override
    public ResponseEntity<?> list(Map<String, Object> queryParams) {
        try {
            int page = parseIntParam(queryParams, "page", 0);
            int size = parseIntParam(queryParams, "size", 20);
            String sortBy = (String) queryParams.getOrDefault("sortBy", "id");
            String sortDir = (String) queryParams.getOrDefault("sortDir", "asc");
            UUID serviceId = null;

            try {
                serviceId = UUID.fromString((String) queryParams.get("serviceId"));
            } catch (Exception e) {
                log.warn("Invalid serviceId provided: {}", queryParams.get("serviceId"));
            }

            Sort sort = sortDir.equalsIgnoreCase("desc")
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();

            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Specialty> resultPage = repository.findAll(pageable, serviceId);

            ApiPaged<Specialty> paged = ApiPaged.of(
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
            throw new RuntimeException("Failed to list entities", e);
        }
    }

}