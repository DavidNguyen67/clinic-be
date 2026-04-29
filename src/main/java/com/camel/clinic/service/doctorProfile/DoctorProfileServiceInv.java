package com.camel.clinic.service.doctorProfile;

import com.camel.clinic.entity.DoctorProfile;
import com.camel.clinic.repository.DoctorProfileRepository;
import com.camel.clinic.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class DoctorProfileServiceInv extends BaseService<DoctorProfile, DoctorProfileRepository> {

    public DoctorProfileServiceInv(DoctorProfileRepository repository) {
        super(DoctorProfile::new, repository);
    }

    @Override
    public ResponseEntity<?> list(Map<String, Object> queryParams) {
//        try {
//            int page = parseIntParam(queryParams, "page", 0);
//            int size = parseIntParam(queryParams, "size", 20);
//            String sortBy = (String) queryParams.getOrDefault("sortBy", "id");
//            String sortDir = (String) queryParams.getOrDefault("sortDir", "asc");
//            UUID serviceId = null;
//
//            try {
//                serviceId = UUID.fromString((String) queryParams.get("serviceId"));
//            } catch (Exception e) {
//                log.warn("Invalid serviceId provided: {}", queryParams.get("serviceId"));
//            }
//
//            Sort sort = sortDir.equalsIgnoreCase("desc")
//                    ? Sort.by(sortBy).descending()
//                    : Sort.by(sortBy).ascending();
//
//            Pageable pageable = PageRequest.of(page, size, sort);
//            Page<Specialty> resultPage = repository.getAllSpecialties(pageable, serviceId);
//
//            ApiPaged<Specialty> paged = ApiPaged.of(
//                    resultPage.getContent(),
//                    resultPage.getTotalElements(),
//                    resultPage.getNumber(),
//                    resultPage.getSize(),
//                    resultPage.getTotalPages()
//            );
//
//            log.info("Listed {} entities (page={}, size={})", resultPage.getNumberOfElements(), page, size);
//            return ResponseEntity.ok(paged);
//        } catch (Exception e) {
//            log.error("Error listing entities: {}", e.getMessage(), e);
//            throw new RuntimeException("Failed to list entities", e);
//        }
        return null;
    }

}