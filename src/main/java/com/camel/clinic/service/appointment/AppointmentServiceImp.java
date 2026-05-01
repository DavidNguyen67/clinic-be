package com.camel.clinic.service.appointment;

import com.camel.clinic.dto.ApiPaged;
import com.camel.clinic.dto.appointment.CreateAppointmentDto;
import com.camel.clinic.dto.appointment.ResponseAppointmentDto;
import com.camel.clinic.dto.appointment.UpdateAppointmentDto;
import com.camel.clinic.entity.*;
import com.camel.clinic.exception.BadRequestException;
import com.camel.clinic.service.CommonService;
import com.camel.clinic.service.SlotLockService;
import com.camel.clinic.service.doctorProfile.DoctorProfileServiceInv;
import com.camel.clinic.service.doctorScheduleException.DoctorScheduleExceptionServiceInv;
import com.camel.clinic.service.patientProfile.PatientProfileServiceInv;
import com.camel.clinic.service.specialty.SpecialtyServiceInv;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.camel.clinic.entity.Appointment.AppointmentStatus.*;

@Service
@Slf4j
@AllArgsConstructor
public class AppointmentServiceImp implements AppointmentService {
    private final AppointmentServiceInv serviceInv;
    private final PatientProfileServiceInv patientProfileServiceInv;
    private final DoctorProfileServiceInv doctorProfileServiceInv;
    private final SpecialtyServiceInv specialtyServiceInv;
    private final DoctorScheduleExceptionServiceInv doctorScheduleExceptionServiceInv;
    private final SlotLockService slotLockService;
    private final CommonService commonService;

    @Override
    public ResponseEntity<?> count() {
        return serviceInv.count();
    }

    @Override
    public ResponseEntity<?> retrieve(String id) {
        return serviceInv.retrieve(id, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> create(CreateAppointmentDto requestBody) {
        String doctorProfileId = requestBody.getDoctorProfileId();
        Date appointmentDate = requestBody.getAppointmentDate();

        DoctorProfile doctorProfile = doctorProfileServiceInv.retrieve(doctorProfileId, null).getBody() instanceof DoctorProfile dp ? dp : null;
        if (doctorProfile == null) {
            throw new IllegalArgumentException("Doctor Profile with ID " + doctorProfileId + " not found");
        }
        validateDoctorNotOnLeave(doctorProfileId, appointmentDate);

        User doctor = doctorProfile.getUser();

        String patientProfileId = requestBody.getPatientProfileId();
        PatientProfile patientProfile = patientProfileServiceInv.retrieve(patientProfileId, null).getBody() instanceof PatientProfile pp ? pp : null;
        if (patientProfile == null) {
            throw new IllegalArgumentException("Patient Profile with ID " + patientProfileId + " not found");
        }

        String specialtyId = doctorProfile.getSpecialty().getId().toString();
        Specialty specialty = specialtyServiceInv.retrieve(specialtyId, null).getBody() instanceof Specialty s ? s : null;
        if (specialty == null) {
            throw new IllegalArgumentException("Specialty with ID " + specialtyId + " not found");
        }

        if (specialty.getIsActive() == Boolean.FALSE) {
            throw new IllegalArgumentException("Specialty with ID " + specialtyId + " is not active");
        }

        UUID lockRef = UUID.randomUUID();
        if (!slotLockService.tryLock(doctor.getId(), appointmentDate, lockRef)) {
            throw new BadRequestException("Slot is being booked by another request");
        }

        List<Appointment> existingAppointments = getCurrentNumberOfAppointment(doctorProfileId, appointmentDate);

        if (!existingAppointments.isEmpty()) {
            throw new IllegalArgumentException("Doctor Profile with ID " + doctorProfileId + " already has an appointment on " + appointmentDate);
        }

        Appointment appointment = new Appointment();
        appointment.setAppointmentCode(commonService.generateAppointmentCode());
        appointment.setDoctorProfile(doctorProfile);
        appointment.setPatientProfile(patientProfile);
        appointment.setSpecialty(specialty);
        appointment.setAppointmentDate(appointmentDate);
        appointment.setBookingType(requestBody.getBookingType());
        appointment.setReason(requestBody.getReason());
        appointment.setSymptoms(requestBody.getSymptoms());
        appointment.setNotes(requestBody.getNotes());
        appointment.setStatus(Appointment.AppointmentStatus.PENDING);

        ResponseEntity<Appointment> saved = (ResponseEntity<Appointment>) serviceInv.create(appointment);
        return ResponseEntity.ok(ResponseAppointmentDto.from(saved.getBody()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> update(String id, UpdateAppointmentDto requestBody) {
        Appointment appointment = serviceInv.retrieve(id, null).getBody() instanceof Appointment a ? a : null;
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment with ID " + id + " not found");
        }

        // Không cho update nếu đã completed, cancelled, no_show
        if (appointment.getStatus() == COMPLETED
                || appointment.getStatus() == CANCELLED
                || appointment.getStatus() == Appointment.AppointmentStatus.NO_SHOW) {
            throw new BadRequestException("Cannot update appointment with status " + appointment.getStatus());
        }

        // Doctor
        if (requestBody.getDoctorProfileId() != null) {
            DoctorProfile doctorProfile = doctorProfileServiceInv.retrieve(requestBody.getDoctorProfileId(), null).getBody() instanceof DoctorProfile dp ? dp : null;
            if (doctorProfile == null) {
                throw new IllegalArgumentException("Doctor Profile with ID " + requestBody.getDoctorProfileId() + " not found");
            }

            // Nếu đổi doctor hoặc đổi ngày → check conflict slot
            Date targetDate = requestBody.getAppointmentDate() != null
                    ? requestBody.getAppointmentDate()
                    : appointment.getAppointmentDate();

            validateDoctorNotOnLeave(requestBody.getDoctorProfileId(), targetDate);


            UUID lockRef = UUID.randomUUID();
            if (!slotLockService.tryLock(doctorProfile.getUser().getId(), targetDate, lockRef)) {
                throw new BadRequestException("Slot is being booked by another request");
            }

            List<Appointment> conflicts = getCurrentNumberOfAppointment(
                    requestBody.getDoctorProfileId(), targetDate
            ).stream()
                    .filter(a -> !a.getId().equals(appointment.getId())) // loại chính nó
                    .toList();

            if (!conflicts.isEmpty()) {
                throw new BadRequestException("Doctor already has an appointment on " + targetDate);
            }

            String specialtyId = doctorProfile.getSpecialty().getId().toString();
            Specialty specialty = specialtyServiceInv.retrieve(specialtyId, null).getBody() instanceof Specialty s ? s : null;
            if (specialty == null) {
                throw new IllegalArgumentException("Specialty with ID " + specialtyId + " not found");
            }

            if (specialty.getIsActive() == Boolean.FALSE) {
                throw new IllegalArgumentException("Specialty with ID " + specialtyId + " is not active");
            }

            appointment.setSpecialty(specialty);
            appointment.setDoctorProfile(doctorProfile);

        } else if (requestBody.getAppointmentDate() != null) {
            validateDoctorNotOnLeave(
                    appointment.getDoctorProfile().getId().toString(),
                    requestBody.getAppointmentDate()
            );

            // Không đổi doctor nhưng đổi ngày → vẫn phải check conflict
            UUID lockRef = UUID.randomUUID();
            if (!slotLockService.tryLock(appointment.getDoctorProfile().getUser().getId(), requestBody.getAppointmentDate(), lockRef)) {
                throw new BadRequestException("Slot is being booked by another request");
            }

            List<Appointment> conflicts = getCurrentNumberOfAppointment(
                    appointment.getDoctorProfile().getId().toString(), requestBody.getAppointmentDate()
            ).stream()
                    .filter(a -> !a.getId().equals(appointment.getId()))
                    .toList();

            if (!conflicts.isEmpty()) {
                throw new BadRequestException("Doctor already has an appointment on " + requestBody.getAppointmentDate());
            }
        }
        appointment.setAppointmentDate(requestBody.getAppointmentDate());
        if (requestBody.getStatus() != null) {
            validateStatusTransition(appointment.getStatus(), requestBody.getStatus());
            appointment.setStatus(requestBody.getStatus());
        }
        appointment.setBookingType(requestBody.getBookingType());
        appointment.setReason(requestBody.getReason());
        appointment.setSymptoms(requestBody.getSymptoms());
        appointment.setNotes(requestBody.getNotes());
        appointment.setQueueNumber(requestBody.getQueueNumber());

        ResponseEntity<Appointment> saved = (ResponseEntity<Appointment>) serviceInv.update(id, appointment, null);

        return ResponseEntity.ok(ResponseAppointmentDto.from(saved.getBody()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
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

    private List<Appointment> getCurrentNumberOfAppointment(String doctorProfileId, Date appointmentDate) {
        Map<String, Object> queryParams = Map.of(
                "doctorProfileId", commonService.parseUuid(doctorProfileId),
                "appointmentDate", appointmentDate,
                "status", List.of(
                        PENDING,
                        CONFIRMED,
                        CHECKED_IN,
                        IN_PROGRESS
                )
        );
        ResponseEntity<?> response = serviceInv.list(queryParams);

        if (response.getStatusCode().is2xxSuccessful()) {
            ApiPaged<Appointment> responseBody = (ApiPaged<Appointment>) response.getBody();

            assert responseBody != null;
            return responseBody.getData().stream()
                    .filter(Objects::nonNull)
                    .toList();
        } else {
            throw new RuntimeException("Failed to retrieve appointments for doctor profile ID " + doctorProfileId + " and date " + appointmentDate + ": " + response.getBody());
        }
    }

    private void validateStatusTransition(Appointment.AppointmentStatus current, Appointment.AppointmentStatus next) {
        Map<Appointment.AppointmentStatus, List<Appointment.AppointmentStatus>> allowed = Map.of(
                Appointment.AppointmentStatus.PENDING, List.of(CONFIRMED, CANCELLED),
                CONFIRMED, List.of(CHECKED_IN, CANCELLED),
                Appointment.AppointmentStatus.CHECKED_IN, List.of(IN_PROGRESS, NO_SHOW),
                Appointment.AppointmentStatus.IN_PROGRESS, List.of(COMPLETED)
        );

        List<Appointment.AppointmentStatus> validNext = allowed.getOrDefault(current, List.of());
        if (!validNext.contains(next)) {
            throw new BadRequestException("Cannot transition from " + current + " to " + next);
        }
    }

    private void validateDoctorNotOnLeave(String doctorProfileId, Date appointmentDate) {
        Map<String, Object> params = new HashMap<>();
        params.put("doctorId", doctorProfileId);
        params.put("exceptionDate", appointmentDate);
        params.put("type", DoctorScheduleException.ExceptionType.LEAVE.name());

        ResponseEntity<?> response = doctorScheduleExceptionServiceInv.list(params);

        if (response.getBody() instanceof ApiPaged<?> paged && !paged.getData().isEmpty()) {
            throw new BadRequestException("Doctor is on leave on " + commonService.formatDate(appointmentDate, "dd/MM/yyyy"));
        }
    }
}
