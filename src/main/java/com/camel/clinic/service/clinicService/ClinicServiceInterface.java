package com.camel.clinic.service.clinicService;

import com.camel.clinic.dto.clinicservice.ClinicServiceUpsertRequestDTO;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface ClinicServiceInterface {
    ResponseEntity<?> list(Map<String, Object> queryParams);

    ResponseEntity<?> getById(String id);

    ResponseEntity<?> create(ClinicServiceUpsertRequestDTO requestDTO);

    ResponseEntity<?> update(String id, ClinicServiceUpsertRequestDTO requestDTO);

    ResponseEntity<?> delete(String id);
}
