package com.camel.clinic.service.patientProfile;

import com.camel.clinic.dto.patientProfile.CreatePatientProfileDto;
import com.camel.clinic.dto.patientProfile.UpdatePatientProfileDto;
import com.camel.clinic.entity.PatientProfile;
import com.camel.clinic.entity.User;
import com.camel.clinic.service.CommonService;
import com.camel.clinic.service.user.UserServiceInv;
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
    public ResponseEntity<?> create(CreatePatientProfileDto requestBody) {
        PatientProfile patientProfile = new PatientProfile();
        patientProfile.setBloodType(requestBody.getBloodType());
        patientProfile.setAllergies(requestBody.getAllergies());
        patientProfile.setPatientCode(commonService.generatePatientCode());
        patientProfile.setAddress(requestBody.getAddress());
        patientProfile.setInsuranceNumber(requestBody.getInsuranceNumber());
        patientProfile.setChronicDiseases(requestBody.getChronicDiseases());
        patientProfile.setLoyaltyPoints(0);
        patientProfile.setTotalVisits(0);

        String userId = requestBody.getUserId();
        User user = userServiceInv.retrieve(userId, null).getBody() instanceof User u ? u : null;
        if (user == null) {
            throw new IllegalArgumentException("User with ID " + userId + " not found");
        }
        commonService.requireRole(user, "PATIENT");
        patientProfile.setUser(user);

        return serviceInv.create(patientProfile);
    }

    @Override
    public ResponseEntity<?> update(String id, UpdatePatientProfileDto requestBody) {
        PatientProfile patientProfile = new PatientProfile();
        patientProfile.setBloodType(requestBody.getBloodType());
        patientProfile.setAllergies(requestBody.getAllergies());
        patientProfile.setAddress(requestBody.getAddress());
        patientProfile.setInsuranceNumber(requestBody.getInsuranceNumber());
        patientProfile.setChronicDiseases(requestBody.getChronicDiseases());

        return serviceInv.update(id, patientProfile, null);
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
