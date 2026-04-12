package com.camel.clinic.service.specialty;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class SpecialtyServiceImp implements SpecialtyService {
    private final SpecialtyServiceInv specialtyServiceInv;

    public ResponseEntity<?> countAllSpecialties() {
        return specialtyServiceInv.count();
    }

    public ResponseEntity<?> getAllSpecialties(Map<String, Object> queryParams) {
        return specialtyServiceInv.list(queryParams);
    }
}
