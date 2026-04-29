package com.camel.clinic.service.staffProfile;

import com.camel.clinic.dto.staffProfile.CreateStaffProfileDto;
import com.camel.clinic.dto.staffProfile.UpdateStaffProfileDto;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface StaffProfileService {
    ResponseEntity<?> list(Map<String, Object> queryParams);

    ResponseEntity<?> count();

    ResponseEntity<?> retrieve(String id);

    ResponseEntity<?> create(CreateStaffProfileDto requestBody);

    ResponseEntity<?> update(String id, UpdateStaffProfileDto requestBody);

    ResponseEntity<?> delete(String id);

    ResponseEntity<?> restore(String id);
}
