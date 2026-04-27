package com.camel.clinic.service.appointment;

import com.camel.clinic.dto.api.ApiPaged;
import com.camel.clinic.entity.*;
import com.camel.clinic.exception.NotFoundException;
import com.camel.clinic.exception.UnauthorizedException;
import com.camel.clinic.repository.AppointmentRepository;
import com.camel.clinic.repository.DoctorRepository;
import com.camel.clinic.repository.PatientRepository;
import com.camel.clinic.repository.UserRepository;
import com.camel.clinic.service.BaseService;
import com.camel.clinic.service.CommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class AppointmentServiceInv extends BaseService<Appointment, AppointmentRepository> {
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final CommonService commonService;

    public AppointmentServiceInv(AppointmentRepository repository, UserRepository userRepository,
                                 PatientRepository patientRepository, DoctorRepository doctorRepository, CommonService commonService) {
        super(Appointment::new, repository);
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = repository;
        this.commonService = commonService;
    }

    public ResponseEntity<?> listAppointments(Map<String, Object> queryParams) {
        try {
            User currentUser = getCurrentUser();
            String role = currentUser.getRole().name();
            Pageable pageable = buildPageable(queryParams);
            Page<Appointment> resultPage;

            if (Role.RoleName.PATIENT.name().equals(role)) {
                Patient patient = patientRepository.findByUserId(currentUser.getId())
                        .orElseThrow(() -> new NotFoundException("Patient profile not found"));
                resultPage = appointmentRepository.findByPatientId(patient.getId(), pageable);

            } else if (Role.RoleName.DOCTOR.name().equals(role)) {
                Doctor doctor = doctorRepository.findByUserId(currentUser.getId())
                        .orElseThrow(() -> new NotFoundException("Doctor profile not found"));
                resultPage = appointmentRepository.findByDoctorId(doctor.getId(), pageable);

            } else {
                resultPage = appointmentRepository.findAll(pageable);
            }

            ApiPaged<Appointment> paged = ApiPaged.of(
                    resultPage.getContent(),
                    resultPage.getTotalElements(),
                    resultPage.getNumber(),
                    resultPage.getSize(),
                    resultPage.getTotalPages()
            );

            log.info("Listed {} appointments for role={}", resultPage.getNumberOfElements(), role);
            return ResponseEntity.ok(paged);

        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("List appointments error", e);
            throw new RuntimeException("Failed to list appointments", e);
        }
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private Pageable buildPageable(Map<String, Object> queryParams) {
        int page = commonService.parseIntParam(queryParams, "page", 0);
        int size = commonService.parseIntParam(queryParams, "size", 20);
        String sortBy = (String) queryParams.getOrDefault("sortBy", "id");
        String sortDir = (String) queryParams.getOrDefault("sortDir", "asc");
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        return PageRequest.of(page, size, sort);
    }


}
