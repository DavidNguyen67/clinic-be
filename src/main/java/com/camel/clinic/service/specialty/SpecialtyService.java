package com.camel.clinic.service.specialty;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface SpecialtyService {
    ResponseEntity<?> getAllSpecialties(Map<String, Object> queryParams);

    ResponseEntity<?> countAllSpecialties();
}
