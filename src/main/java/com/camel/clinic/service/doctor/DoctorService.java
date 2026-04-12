package com.camel.clinic.service.doctor;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface DoctorService {
    ResponseEntity<?> countAllDoctors();

    ResponseEntity<?> getTopDoctors();

    ResponseEntity<?> filterDoctors(Map<String, Object> queryParams);
}
