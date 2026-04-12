package com.camel.clinic.service.doctor;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface DoctorService {
    ResponseEntity<?> getAllDoctors(Map<String, Object> queryParams);

    ResponseEntity<?> countAllDoctors();

    ResponseEntity<?> getTopDoctors();
}
