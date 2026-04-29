package com.camel.clinic.service.services;

import com.camel.clinic.dto.ApiPaged;
import com.camel.clinic.entity.ClinicService;
import com.camel.clinic.repository.ServicesRepository;
import com.camel.clinic.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class ServicesServiceInv extends BaseService<ClinicService, ServicesRepository> {

    public ServicesServiceInv(ServicesRepository repository) {
        super(ClinicService::new, repository);
    }

    @Override
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
            Page<ClinicService> resultPage = repository.getAll(pageable);

            ApiPaged<ClinicService> paged = ApiPaged.of(
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