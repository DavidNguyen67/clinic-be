package com.camel.clinic.service.patient;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class PatientServiceImp implements PatientService {
    private final PatientServiceInv patientServiceInv;

    public ResponseEntity<?> getAllPatients(Map<String, Object> queryParams) {
        return patientServiceInv.list(queryParams);
    }

    public ResponseEntity<?> countAllPatients() {
        return patientServiceInv.count();
    }
}
