package com.camel.clinic.service.clinicService;

import com.camel.clinic.dto.api.ApiPaged;
import com.camel.clinic.entity.ClinicService;
import com.camel.clinic.repository.ClinicServiceRepository;
import com.camel.clinic.service.BaseService;
import com.camel.clinic.service.CommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class ClinicServiceInv extends BaseService<ClinicService, ClinicServiceRepository> {
    private final CommonService commonService;

    public ClinicServiceInv(ClinicServiceRepository repository, CommonService commonService) {
        super(ClinicService::new, repository);
        this.commonService = commonService;
    }

    @Override
    public ResponseEntity<?> list(Map<String, Object> queryParams) {
        try {
            int page = commonService.parseIntParam(queryParams, "page", 0);
            int size = commonService.parseIntParam(queryParams, "size", 20);
            String sortBy = (String) queryParams.getOrDefault("sortBy", "id");
            String sortDir = (String) queryParams.getOrDefault("sortDir", "asc");

            Sort sort = sortDir.equalsIgnoreCase("desc")
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();

            Pageable pageable = PageRequest.of(page, size, sort);
            Page<ClinicService> resultPage = repository.findAll(pageable);

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
