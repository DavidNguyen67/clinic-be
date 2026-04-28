package com.camel.clinic.service.specialty;

import com.camel.clinic.dto.specialty.CreateSpecialtyDto;
import com.camel.clinic.dto.specialty.UpdateSpecialtyDto;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface SpecialtyService {
    ResponseEntity<?> getAllSpecialties(Map<String, Object> queryParams);

    ResponseEntity<?> countAllSpecialties();

    ResponseEntity<?> getSpecialtyById(String id);

    ResponseEntity<?> createSpecialty(CreateSpecialtyDto requestBody);

    ResponseEntity<?> updateSpecialty(String id, UpdateSpecialtyDto requestBody);

    ResponseEntity<?> deleteSpecialty(String id);
}
