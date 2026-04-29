package com.camel.clinic.service.patientProfile;

import com.camel.clinic.dto.patientProfile.CreatePatientProfileDto;
import com.camel.clinic.dto.patientProfile.UpdatePatientProfileDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class PatientProfileServiceImp implements PatientProfileService {
    private final PatientProfileServiceInv patientProfileServiceInv;

    @Override
    public ResponseEntity<?> count() {
        return patientProfileServiceInv.count();
    }

    @Override
    public ResponseEntity<?> retrieve(String id) {
        return patientProfileServiceInv.retrieve(id, null);
    }

    @Override
    public ResponseEntity<?> create(CreatePatientProfileDto requestBody) {
        return null;
    }

    @Override
    public ResponseEntity<?> update(String id, UpdatePatientProfileDto requestBody) {
        return null;
    }

    @Override
    public ResponseEntity<?> delete(String id) {
        return patientProfileServiceInv.delete(id);
    }

    @Override
    public ResponseEntity<?> restore(String id) {
        return patientProfileServiceInv.restore(id);
    }

    @Override
    public ResponseEntity<?> list(Map<String, Object> queryParams) {
        return patientProfileServiceInv.list(queryParams);
    }
}
