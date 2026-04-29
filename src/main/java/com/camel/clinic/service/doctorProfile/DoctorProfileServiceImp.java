package com.camel.clinic.service.doctorProfile;

import com.camel.clinic.dto.doctorProfile.CreateDoctorProfileDto;
import com.camel.clinic.dto.doctorProfile.UpdateDoctorProfileDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class DoctorProfileServiceImp implements DoctorProfileService {
    private final DoctorProfileServiceInv specialtyServiceInv;

    @Override
    public ResponseEntity<?> count() {
        return specialtyServiceInv.count();
    }

    @Override
    public ResponseEntity<?> retrieve(String id) {
        return specialtyServiceInv.retrieve(id, null);
    }

    @Override
    public ResponseEntity<?> create(CreateDoctorProfileDto requestBody) {
        return null;
    }

    @Override
    public ResponseEntity<?> update(String id, UpdateDoctorProfileDto requestBody) {
        return null;
    }


    @Override
    public ResponseEntity<?> delete(String id) {
        return specialtyServiceInv.delete(id);
    }

    @Override
    public ResponseEntity<?> restore(String id) {
        return specialtyServiceInv.restore(id);
    }

    public ResponseEntity<?> list(Map<String, Object> queryParams) {
        return specialtyServiceInv.list(queryParams);
    }
}
