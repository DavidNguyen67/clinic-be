package com.camel.clinic.service.staffProfile;

import com.camel.clinic.dto.staffProfile.CreateStaffProfileDto;
import com.camel.clinic.dto.staffProfile.UpdateStaffProfileDto;
import com.camel.clinic.entity.StaffProfile;
import com.camel.clinic.entity.User;
import com.camel.clinic.service.CommonService;
import com.camel.clinic.service.user.UserServiceInv;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class StaffProfileServiceImp implements StaffProfileService {
    private final StaffProfileServiceInv serviceInv;
    private final CommonService commonService;
    private final UserServiceInv userServiceInv;

    @Override
    public ResponseEntity<?> count() {
        return serviceInv.count();
    }

    @Override
    public ResponseEntity<?> retrieve(String id) {
        return serviceInv.retrieve(id, null);
    }

    @Override
    public ResponseEntity<?> create(CreateStaffProfileDto requestBody) {
        StaffProfile staffProfile = new StaffProfile();
        staffProfile.setPosition(requestBody.getPosition());
        staffProfile.setDepartment(requestBody.getDepartment());
        Date hireDate = commonService.parseToDate(requestBody.getHireDate());
        staffProfile.setHireDate(hireDate);

        String userId = requestBody.getUserId();
        if (userId != null && !userId.isEmpty()) {
            User user = userServiceInv.retrieve(userId, null).getBody() instanceof User u ? u : null;
            if (user == null) {
                throw new IllegalArgumentException("User with ID " + userId + " not found");
            }
            staffProfile.setUser(user);
        }

        return serviceInv.create(staffProfile);
    }

    @Override
    public ResponseEntity<?> update(String id, UpdateStaffProfileDto requestBody) {
        StaffProfile staffProfile = new StaffProfile();
        staffProfile.setPosition(requestBody.getPosition());
        staffProfile.setDepartment(requestBody.getDepartment());
        Date hireDate = commonService.parseToDate(requestBody.getHireDate());
        staffProfile.setHireDate(hireDate);

        return serviceInv.update(id, staffProfile, null);
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
