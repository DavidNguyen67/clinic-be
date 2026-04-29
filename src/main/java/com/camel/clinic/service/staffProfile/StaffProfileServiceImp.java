package com.camel.clinic.service.staffProfile;

import com.camel.clinic.dto.staffProfile.CreateStaffProfileDto;
import com.camel.clinic.dto.staffProfile.UpdateStaffProfileDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class StaffProfileServiceImp implements StaffProfileService {
    private final StaffProfileServiceInv staffProfileServiceInv;

    @Override
    public ResponseEntity<?> count() {
        return staffProfileServiceInv.count();
    }

    @Override
    public ResponseEntity<?> retrieve(String id) {
        return staffProfileServiceInv.retrieve(id, null);
    }

    @Override
    public ResponseEntity<?> create(CreateStaffProfileDto requestBody) {
        return null;
    }

    @Override
    public ResponseEntity<?> update(String id, UpdateStaffProfileDto requestBody) {
        return null;
    }

    @Override
    public ResponseEntity<?> delete(String id) {
        return staffProfileServiceInv.delete(id);
    }

    @Override
    public ResponseEntity<?> restore(String id) {
        return staffProfileServiceInv.restore(id);
    }

    @Override
    public ResponseEntity<?> list(Map<String, Object> queryParams) {
        return staffProfileServiceInv.list(queryParams);
    }
}
