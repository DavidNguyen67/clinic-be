package com.camel.clinic.service.patient;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface PatientService {
    ResponseEntity<?> getAllPatients(Map<String, Object> queryParams);

    ResponseEntity<?> countAllPatients();
}
