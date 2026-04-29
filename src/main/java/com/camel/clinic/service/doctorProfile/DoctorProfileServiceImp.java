package com.camel.clinic.service.doctorProfile;

import com.camel.clinic.dto.doctorProfile.CreateDoctorProfileDto;
import com.camel.clinic.dto.doctorProfile.UpdateDoctorProfileDto;
import com.camel.clinic.entity.DoctorProfile;
import com.camel.clinic.entity.Specialty;
import com.camel.clinic.entity.User;
import com.camel.clinic.service.specialty.SpecialtyServiceInv;
import com.camel.clinic.service.user.UserServiceInv;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class DoctorProfileServiceImp implements DoctorProfileService {
    private final DoctorProfileServiceInv doctorProfileServiceInv;
    private final UserServiceInv userServiceInv;
    private final SpecialtyServiceInv specialtyServiceInv;

    @Override
    public ResponseEntity<?> count() {
        return doctorProfileServiceInv.count();
    }

    @Override
    public ResponseEntity<?> retrieve(String id) {
        return doctorProfileServiceInv.retrieve(id, null);
    }

    @Override
    public ResponseEntity<?> create(CreateDoctorProfileDto requestBody) {
        DoctorProfile doctorProfile = new DoctorProfile();
        doctorProfile.setDoctorCode(requestBody.getDoctorCode());
        doctorProfile.setDegree(requestBody.getDegree());
        doctorProfile.setExperienceYears(requestBody.getExperienceYears());
        doctorProfile.setEducation(requestBody.getEducation());
        doctorProfile.setBio(requestBody.getBio());
        doctorProfile.setConsultationFee(requestBody.getConsultationFee());
        doctorProfile.setIsFeatured(requestBody.getIsFeatured());

        String specialtyId = requestBody.getSpecialtyId();
        if (specialtyId != null && !specialtyId.isEmpty()) {
            Specialty specialty = specialtyServiceInv.retrieve(specialtyId, null).getBody() instanceof Specialty sp ? sp : null;
            if (specialty == null) {
                throw new IllegalArgumentException("Specialty with ID " + specialtyId + " not found");
            }
            doctorProfile.setSpecialty(specialty);
        }

        String userId = requestBody.getUserId();
        if (userId != null && !userId.isEmpty()) {
            User user = userServiceInv.retrieve(userId, null).getBody() instanceof User u ? u : null;
            if (user == null) {
                throw new IllegalArgumentException("User with ID " + userId + " not found");
            }
            doctorProfile.setUser(user);
        }

        return doctorProfileServiceInv.create(doctorProfile);
    }

    @Override
    public ResponseEntity<?> update(String id, UpdateDoctorProfileDto requestBody) {
        DoctorProfile doctorProfile = new DoctorProfile();
        doctorProfile.setDoctorCode(requestBody.getDoctorCode());
        doctorProfile.setDegree(requestBody.getDegree());
        doctorProfile.setExperienceYears(requestBody.getExperienceYears());
        doctorProfile.setEducation(requestBody.getEducation());
        doctorProfile.setBio(requestBody.getBio());
        doctorProfile.setConsultationFee(requestBody.getConsultationFee());
        doctorProfile.setIsFeatured(requestBody.getIsFeatured());

        String specialtyId = requestBody.getSpecialtyId();
        if (specialtyId != null && !specialtyId.isEmpty()) {
            Specialty specialty = specialtyServiceInv.retrieve(specialtyId, null).getBody() instanceof Specialty sp ? sp : null;
            if (specialty == null) {
                throw new IllegalArgumentException("Specialty with ID " + specialtyId + " not found");
            }
            doctorProfile.setSpecialty(specialty);
        }

        String userId = requestBody.getUserId();
        if (userId != null && !userId.isEmpty()) {
            User user = userServiceInv.retrieve(userId, null).getBody() instanceof User u ? u : null;
            if (user == null) {
                throw new IllegalArgumentException("User with ID " + userId + " not found");
            }
            doctorProfile.setUser(user);
        }

        return doctorProfileServiceInv.update(id, doctorProfile, null);
    }

    @Override
    public ResponseEntity<?> delete(String id) {
        return doctorProfileServiceInv.delete(id);
    }

    @Override
    public ResponseEntity<?> restore(String id) {
        return doctorProfileServiceInv.restore(id);
    }

    @Override
    public ResponseEntity<?> list(Map<String, Object> queryParams) {
        return doctorProfileServiceInv.list(queryParams);
    }
}
