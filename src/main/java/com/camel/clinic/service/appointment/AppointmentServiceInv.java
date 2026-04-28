package com.camel.clinic.service.appointment;

import com.camel.clinic.dto.api.ApiPaged;
import com.camel.clinic.entity.*;
import com.camel.clinic.exception.NotFoundException;
import com.camel.clinic.repository.AppointmentRepository;
import com.camel.clinic.repository.DoctorRepository;
import com.camel.clinic.repository.PatientRepository;
import com.camel.clinic.service.BaseService;
import com.camel.clinic.service.CommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
@Slf4j
public class AppointmentServiceInv extends BaseService<Appointment, AppointmentRepository> {
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final CommonService commonService;

    public AppointmentServiceInv(AppointmentRepository repository,
                                 PatientRepository patientRepository, DoctorRepository doctorRepository, CommonService commonService) {
        super(Appointment::new, repository);
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = repository;
        this.commonService = commonService;
    }

    public ResponseEntity<?> listAppointments(Map<String, Object> queryParams) {
        try {
            User currentUser = commonService.getCurrentUser();
            String role = currentUser.getRole().name();
            Pageable pageable = commonService.buildPageable(queryParams);
            Date appointmentDate = commonService.parseAppointmentDate(queryParams.get("appointmentDate"));

            Page<Appointment> resultPage;

            if (Role.RoleName.PATIENT.name().equals(role)) {
                Patient patient = patientRepository.findByUserId(currentUser.getId())
                        .orElseThrow(() -> new NotFoundException("Patient profile not found"));
                resultPage = appointmentRepository.findByPatientIdAndDate(
                        patient.getId(), appointmentDate, pageable);

            } else if (Role.RoleName.DOCTOR.name().equals(role)) {
                Doctor doctor = doctorRepository.findByUserId(currentUser.getId())
                        .orElseThrow(() -> new NotFoundException("Doctor profile not found"));
                resultPage = appointmentRepository.findByDoctorIdAndDate(
                        doctor.getId(), appointmentDate, pageable);

            } else {
                // STAFF / ADMIN — reuse existing query that already supports date filter
                String patientName = queryParams.get("patientName") != null
                        ? queryParams.get("patientName").toString()
                        : null;
                resultPage = appointmentRepository.findStaffAppointments(
                        patientName, appointmentDate, pageable);
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
}
