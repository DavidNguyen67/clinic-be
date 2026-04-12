package com.camel.clinic.service.patient;

import com.camel.clinic.entity.Patient;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class PatientServiceImp implements PatientService {
    private final PatientServiceInv patientServiceInv;

    public ResponseEntity<?> createDoctor(Patient body) throws JsonProcessingException {
        return patientServiceInv.create(body);
    }
}
