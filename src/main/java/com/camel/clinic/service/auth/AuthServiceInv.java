package com.camel.clinic.service.auth;

import com.camel.clinic.dto.auth.UserProfileDTO;
import com.camel.clinic.entity.DoctorProfile;
import com.camel.clinic.entity.PatientProfile;
import com.camel.clinic.entity.Role;
import com.camel.clinic.entity.User;
import com.camel.clinic.repository.DoctorProfileRepository;
import com.camel.clinic.repository.PatientProfileRepository;
import com.camel.clinic.repository.UserRepository;
import com.camel.clinic.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class AuthServiceInv extends BaseService<User, UserRepository> {
    private final DoctorProfileRepository doctorProfileRepository;
    private final PatientProfileRepository patientProfileRepository;

    public AuthServiceInv(UserRepository repository, DoctorProfileRepository doctorProfileRepository,
                          PatientProfileRepository patientProfileRepository) {
        super(User::new, repository);
        this.doctorProfileRepository = doctorProfileRepository;
        this.patientProfileRepository = patientProfileRepository;
    }

    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    public Optional<User> findById(UUID id) {
        return repository.findById(id);
    }

    public User save(User user) {
        return repository.save(user);
    }

    public Optional<User> findByPhone(String phone) {
        return repository.findByPhone(phone);
    }

    public List<String> findEmailsBatch(Pageable pageable) {
        return repository.findEmailsBatch(pageable);
    }

    public UserProfileDTO buildUserProfileDTO(User user) {
        UserProfileDTO profile = new UserProfileDTO();
        profile.setId(user.getId());
        profile.setEmail(user.getEmail());
        profile.setFullName(user.getFullName());
        profile.setPhone(user.getPhone());
        profile.setRole(String.valueOf(user.getRole()));
        profile.setGender(String.valueOf(user.getGender()));
        profile.setDateOfBirth(user.getDateOfBirth());
        profile.setPathAvatar(user.getPathAvatar());
        profile.setStatus(user.getStatus().toString());
        profile.setEmailVerified(user.getEmailVerified());
        profile.setPhoneVerified(user.getPhoneVerified());
        if (user.getLastLogin() != null) {
            profile.setLastLogin(user.getLastLogin());
        }

        // Load doctor info if user is a doctor
        if (Role.RoleName.DOCTOR.equals(user.getRole())) {
            UUID userId = user.getId();
            Optional<DoctorProfile> doctor = doctorProfileRepository.findByUserId(userId);

            if (doctor.isPresent()) {
//                List<DoctorSchedule> schedules = doctorScheduleRepository.findByDoctorId(doctor.get().getId());
//                List<UserProfileDTO.DoctorProfileDTO.ScheduleDTO> scheduleDTOs = schedules.stream().map(s -> {
//                    UserProfileDTO.DoctorProfileDTO.ScheduleDTO dto = new UserProfileDTO.DoctorProfileDTO.ScheduleDTO();
//                    dto.setId(s.getId());
//                    dto.setDayOfWeek(s.getDayOfWeek());
//                    dto.setStartTime(s.getStartTime().toString());
//                    dto.setEndTime(s.getEndTime().toString());
//                    return dto;
//                }).toList();

                DoctorProfile doc = doctor.get();
                UserProfileDTO.DoctorProfileDTO doctorDTO = new UserProfileDTO.DoctorProfileDTO();
                doctorDTO.setId(doc.getId());
                doctorDTO.setExperienceYears(doc.getExperienceYears());
                doctorDTO.setDegree(doc.getDegree());
                doctorDTO.setEducation(doc.getEducation());
                doctorDTO.setBio(doc.getBio());
                doctorDTO.setConsultationFee(doc.getConsultationFee().doubleValue());
                doctorDTO.setAverageRating(doc.getAverageRating().doubleValue());
                doctorDTO.setTotalReviews(doc.getTotalReviews());
//                doctorDTO.setSchedules(scheduleDTOs);

                // Specialty
                if (doc.getSpecialty() != null) {
                    UserProfileDTO.DoctorProfileDTO.SpecialtyDTO specialtyDTO =
                            new UserProfileDTO.DoctorProfileDTO.SpecialtyDTO();
                    specialtyDTO.setId(doc.getSpecialty().getId());
                    specialtyDTO.setName(doc.getSpecialty().getName());
                    specialtyDTO.setSlug(doc.getSpecialty().getSlug());
                    specialtyDTO.setDescription(doc.getSpecialty().getDescription());
                    doctorDTO.setSpecialty(specialtyDTO);
                }

                profile.setDoctor(doctorDTO);
            }
        }

        // Load patient info if user is a patient
        if (Role.RoleName.PATIENT.equals(user.getRole())) {
            Optional<PatientProfile> patient = patientProfileRepository.findByUserId(user.getId());

            if (patient.isPresent()) {
                PatientProfile pat = patient.get();
                UserProfileDTO.PatientProfileDTO patientDTO = new UserProfileDTO.PatientProfileDTO();
                patientDTO.setId(pat.getId());
                patientDTO.setPatientCode(pat.getPatientCode());
                patientDTO.setAddress(pat.getAddress());
                patientDTO.setInsuranceNumber(pat.getInsuranceNumber());
                patientDTO.setBloodType(String.valueOf(pat.getBloodType()));
                patientDTO.setAllergies(pat.getAllergies());
                patientDTO.setChronicDiseases(pat.getChronicDiseases());
                patientDTO.setLoyaltyPoints(pat.getLoyaltyPoints());
                patientDTO.setTotalVisits(pat.getTotalVisits());

                profile.setPatient(patientDTO);
            }
        }

        return profile;
    }
}