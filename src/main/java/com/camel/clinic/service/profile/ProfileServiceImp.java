package com.camel.clinic.service.profile;

import com.camel.clinic.entity.DoctorProfile;
import com.camel.clinic.entity.PatientProfile;
import com.camel.clinic.service.doctorProfile.DoctorProfileServiceInv;
import com.camel.clinic.service.patientProfile.PatientProfileServiceInv;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class ProfileServiceImp implements ProfileService {
    private final DoctorProfileServiceInv doctorProfileServiceInv;
    private final PatientProfileServiceInv patientProfileServiceInv;

    @Override
    public ResponseEntity<?> list(Map<String, Object> queryParams) {
        List<String> ids = (List<String>) queryParams.get("ids");

        List<DoctorProfile> doctorProfiles = doctorProfileServiceInv.findInIds(ids);
        List<PatientProfile> patientProfiles = patientProfileServiceInv.findInIds(ids);


        return ResponseEntity.ok(Map.of(
                "doctorProfiles", doctorProfiles,
                "patientProfiles", patientProfiles
        ));
    }
}
