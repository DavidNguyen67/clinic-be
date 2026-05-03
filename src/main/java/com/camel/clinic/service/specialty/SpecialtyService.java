package com.camel.clinic.service.specialty;

import com.camel.clinic.dto.specialty.CreateSpecialtyDto;
import com.camel.clinic.dto.specialty.UpdateSpecialtyDto;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface SpecialtyService {
    ResponseEntity<?> list(Map<String, Object> queryParams);

    ResponseEntity<?> count();

    ResponseEntity<?> retrieve(String id);

    ResponseEntity<?> create(CreateSpecialtyDto requestBody);

    ResponseEntity<?> update(String id, UpdateSpecialtyDto requestBody);

    ResponseEntity<?> delete(String id);

    ResponseEntity<?> restore(String id);

    ResponseEntity<?> listWithDoctorCount(Map<String, Object> queryParams);
}
