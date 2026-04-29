package com.camel.clinic.service.doctorProfile;

import com.camel.clinic.dto.doctorProfile.CreateDoctorProfileDto;
import com.camel.clinic.dto.doctorProfile.UpdateDoctorProfileDto;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface DoctorProfileService {
    ResponseEntity<?> list(Map<String, Object> queryParams);

    ResponseEntity<?> count();

    ResponseEntity<?> retrieve(String id);

    ResponseEntity<?> create(CreateDoctorProfileDto requestBody);

    ResponseEntity<?> update(String id, UpdateDoctorProfileDto requestBody);

    ResponseEntity<?> delete(String id);

    ResponseEntity<?> restore(String id);
}
