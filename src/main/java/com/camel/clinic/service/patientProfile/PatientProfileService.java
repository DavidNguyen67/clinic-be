package com.camel.clinic.service.patientProfile;

import com.camel.clinic.dto.patientProfile.CreatePatientProfileDto;
import com.camel.clinic.dto.patientProfile.UpdatePatientProfileDto;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface PatientProfileService {
    ResponseEntity<?> list(Map<String, Object> queryParams);

    ResponseEntity<?> count();

    ResponseEntity<?> retrieve(String id);

    ResponseEntity<?> create(CreatePatientProfileDto requestBody);

    ResponseEntity<?> update(String id, UpdatePatientProfileDto requestBody);

    ResponseEntity<?> delete(String id);

    ResponseEntity<?> restore(String id);
}
