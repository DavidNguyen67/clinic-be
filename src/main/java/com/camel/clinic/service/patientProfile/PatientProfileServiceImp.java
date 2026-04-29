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
    private final PatientProfileServiceInv serviceInv;

    @Override
    public ResponseEntity<?> count() {
        return serviceInv.count();
    }

    @Override
    public ResponseEntity<?> retrieve(String id) {
        return serviceInv.retrieve(id, null);
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
        return serviceInv.delete(id);
    }

    @Override
    public ResponseEntity<?> restore(String id) {
        return serviceInv.restore(id);
    }

    @Override
    public ResponseEntity<?> list(Map<String, Object> queryParams) {
        return serviceInv.list(queryParams);
    }
}
