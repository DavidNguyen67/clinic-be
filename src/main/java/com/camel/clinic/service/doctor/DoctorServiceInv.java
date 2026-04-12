package com.camel.clinic.service.doctor;

import com.camel.clinic.dto.DoctorDTO;
import com.camel.clinic.entity.Doctor;
import com.camel.clinic.repository.DoctorRepository;
import com.camel.clinic.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class DoctorServiceInv extends BaseService<Doctor, DoctorRepository> {
    public DoctorServiceInv(DoctorRepository repository) {
        super(Doctor::new, repository);
    }

    public ResponseEntity<?> getTopDoctors() {
        List<DoctorDTO> doctors = repository.getTopDoctors();

        return ResponseEntity.ok(doctors);
    }

    public ResponseEntity<?> filterDoctors(Map<String, Object> queryParams) {
        try {
            int page = parseIntParam(queryParams, "page", 0);
            int size = parseIntParam(queryParams, "size", 20);
            String doctorName = (String) queryParams.getOrDefault("doctorName", null);
            String specialtyName = (String) queryParams.getOrDefault("specialtyName", null);
            String specialtyIdStr = (String) queryParams.getOrDefault("specialtyId", null);
            UUID specialtyId = null;
            if (specialtyIdStr != null) {
                try {
                    specialtyId = UUID.fromString(specialtyIdStr);
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid specialtyId format: {}", specialtyIdStr, e);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("error", "Invalid specialtyId format",
                                    "message", "specialtyId must be a valid UUID"));
                }
            }

            Pageable pageable = PageRequest.of(page, size);
            Page<DoctorDTO> resultPage = repository.filterDoctors(doctorName, specialtyName, specialtyId, pageable);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("data", resultPage.getContent());
            response.put("page", resultPage.getNumber());
            response.put("size", resultPage.getSize());
            response.put("totalItems", resultPage.getTotalElements());
            response.put("totalPages", resultPage.getTotalPages());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error filtering doctors: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to filter doctors", "message", e.getMessage()));
        }
    }
}