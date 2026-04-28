package com.camel.clinic.service.patient;

import com.camel.clinic.dto.appointment.AppointmentResponseDTO;
import com.camel.clinic.dto.patient.PatientProfileDTO;
import com.camel.clinic.dto.patient.UpdatePatientProfileDto;
import com.camel.clinic.entity.Appointment;
import com.camel.clinic.entity.Patient;
import com.camel.clinic.entity.Role;
import com.camel.clinic.entity.User;
import com.camel.clinic.exception.NotFoundException;
import com.camel.clinic.exception.UnauthorizedException;
import com.camel.clinic.repository.AppointmentRepository;
import com.camel.clinic.repository.PatientRepository;
import com.camel.clinic.repository.UserRepository;
import com.camel.clinic.service.CommonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PatientProfileServiceImp {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final CommonService commonService;

    @Transactional(readOnly = true)
    public ResponseEntity<?> getProfile() {
        try {
            Patient patient = getCurrentPatient();
            return ResponseEntity.ok(toDto(patient));
        } catch (Exception e) {
            throw handleError(e, "Failed to get patient profile");
        }
    }

    public ResponseEntity<?> updateProfile(UpdatePatientProfileDto requestBody) {
        try {
            Patient patient = getCurrentPatient();
            User user = patient.getUser();
            if (requestBody.getFullName() != null) user.setFullName(requestBody.getFullName());
            if (requestBody.getPhone() != null) user.setPhone(requestBody.getPhone());
            if (requestBody.getAddress() != null) patient.setAddress(requestBody.getAddress());
            if (requestBody.getInsuranceNumber() != null)
                patient.setInsuranceNumber(requestBody.getInsuranceNumber());
            if (requestBody.getAllergies() != null) patient.setAllergies(requestBody.getAllergies());
            if (requestBody.getChronicDiseases() != null) patient.setChronicDiseases(requestBody.getChronicDiseases());

            userRepository.save(user);
            Patient saved = patientRepository.save(patient);
            return ResponseEntity.ok(toDto(saved));
        } catch (Exception e) {
            throw handleError(e, "Failed to update patient profile");
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> getAllAppointmentByPatientId() {
        try {
            Patient patient = getCurrentPatient();
            List<Appointment> appointments = appointmentRepository.findByPatientId(patient.getId());

            List<AppointmentResponseDTO> appointmentDTOs = toResponseDTOList(appointments);

            return ResponseEntity.ok(appointmentDTOs);
        } catch (Exception e) {
            throw handleError(e, "Failed to get patient appointments");
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> getMyHistory() {
        try {
            Patient patient = getCurrentPatient();
//            List<Appointment> history = appointmentRepository.findByPatientId(patient.getId())
//                    .stream()
//                    .filter(a -> a.getStatus() == Appointment.AppointmentStatus.completed)
//                    .toList();
//            return ResponseEntity.ok(history.stream().map(this::toAppointmentSummary).collect(Collectors.toList()));
            return ResponseEntity.ok(patient);
        } catch (Exception e) {
            throw handleError(e, "Failed to get patient history");
        }
    }

    private Patient getCurrentPatient() {
        User user = commonService.getCurrentUser();

        if (user.getRole() != Role.RoleName.PATIENT) {
            throw new UnauthorizedException("Only patient can access this endpoint");
        }

        return patientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NotFoundException("Patient profile not found"));
    }

    private RuntimeException handleError(Exception e, String fallbackMessage) {
        if (e instanceof UnauthorizedException) {
            log.error(fallbackMessage + ": {}", e.getMessage());
            return (UnauthorizedException) e;
        }
        if (e instanceof NotFoundException) {
            log.error(fallbackMessage + ": {}", e.getMessage());
            return (NotFoundException) e;
        }
        log.error(fallbackMessage, e);
        return new RuntimeException(fallbackMessage, e);
    }

    private PatientProfileDTO toDto(Patient p) {
        return new PatientProfileDTO(
                p.getId(),
                p.getPatientCode(),
                p.getUser().getFullName(),
                p.getUser().getEmail(),
                p.getUser().getPhone(),
                p.getUser().getGender().name(),
//                p.getUser().getDateOfBirth(),
                p.getAddress(),
                p.getInsuranceNumber(),
                p.getBloodType() != null ? p.getBloodType().name() : null,
                p.getAllergies(),
                p.getChronicDiseases(),
                p.getLoyaltyPoints(),
                p.getTotalVisits()
        );
    }

    public AppointmentResponseDTO toAppointmentResponseDTO(Appointment appointment) {
        if (appointment == null) return null;

        return AppointmentResponseDTO.builder()
                .id(appointment.getId())
                .appointmentCode(appointment.getAppointmentCode())
                .appointmentDate(appointment.getAppointmentDate())
                .startTime(appointment.getStartTime())
                .endTime(appointment.getEndTime())
                .status(appointment.getStatus() != null ? appointment.getStatus().name() : null)
                .bookingType(appointment.getBookingType() != null ? appointment.getBookingType().name() : null)
                .reason(appointment.getReason())
                .symptoms(appointment.getSymptoms())
                .notes(appointment.getNotes())
                .queueNumber(appointment.getQueueNumber())
                // Doctor fields
                .doctorId(appointment.getDoctor() != null ? appointment.getDoctor().getId() : null)
                .doctorName(appointment.getDoctor() != null ? appointment.getDoctor().getUser().getFullName() : null)
                .doctorPhone(appointment.getDoctor() != null ? appointment.getDoctor().getUser().getPhone() : null)
                .doctorEmail(appointment.getDoctor() != null ? appointment.getDoctor().getUser().getEmail() : null)
                // Patient fields
                .patientId(appointment.getPatient() != null ? appointment.getPatient().getId() : null)
                .patientName(appointment.getPatient() != null ? appointment.getPatient().getUser().getFullName() : null)
                .patientPhone(appointment.getPatient() != null ? appointment.getPatient().getUser().getPhone() : null)
                .patientEmail(appointment.getPatient() != null ? appointment.getPatient().getUser().getEmail() : null)
                // Service fields
                .serviceId(appointment.getClinicService() != null ? appointment.getClinicService().getId() : null)
                .serviceName(appointment.getClinicService() != null ? appointment.getClinicService().getName() : null)
                .price(appointment.getClinicService() != null ? appointment.getClinicService().getPrice() : null)
                .build();
    }

    public List<AppointmentResponseDTO> toResponseDTOList(List<Appointment> appointments) {
        if (appointments == null) return List.of();
        return appointments.stream()
                .map(this::toAppointmentResponseDTO)
                .toList();
    }
}

