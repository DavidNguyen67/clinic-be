package com.camel.clinic.service.appointment;

import com.camel.clinic.dto.appointment.CreateAppointmentDto;
import com.camel.clinic.dto.appointment.ResponseAppointmentDto;
import com.camel.clinic.dto.appointment.UpdateAppointmentDto;
import com.camel.clinic.entity.*;
import com.camel.clinic.exception.BadRequestException;
import com.camel.clinic.repository.AppointmentRepository;
import com.camel.clinic.service.CommonService;
import com.camel.clinic.service.doctorProfile.DoctorProfileServiceInv;
import com.camel.clinic.service.doctorScheduleException.DoctorScheduleExceptionServiceInv;
import com.camel.clinic.service.patientProfile.PatientProfileServiceInv;
import com.camel.clinic.util.AppointmentStatusTransition;
import com.camel.clinic.util.SecuritiesUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.camel.clinic.entity.Appointment.AppointmentStatus.CANCELLED;
import static com.camel.clinic.entity.Appointment.AppointmentStatus.PENDING;

@Service
@Slf4j
@AllArgsConstructor
public class AppointmentServiceImp implements AppointmentService {
    private final AppointmentServiceInv serviceInv;
    private final DoctorScheduleExceptionServiceInv doctorScheduleExceptionServiceInv;
    private final DoctorProfileServiceInv doctorProfileServiceInv;
    private final PatientProfileServiceInv patientProfileServiceInv;
    private final AppointmentRepository appointmentRepository;

    @Override
    public ResponseEntity<?> count() {
        return serviceInv.count();
    }

    @Override
    public ResponseEntity<?> retrieve(String id) {
        return serviceInv.retrieve(id, null);
    }

    @Override
    public ResponseEntity<?> create(CreateAppointmentDto requestBody) {
        String doctorProfileId = requestBody.getDoctorProfileId();
        String patientProfileId = requestBody.getPatientProfileId();
        Date appointmentDate = requestBody.getAppointmentDate();

        if (!doctorScheduleExceptionServiceInv.isDoctorAvailable(doctorProfileId, appointmentDate)) {
            throw new BadRequestException(
                    "Doctor is not available at the selected time. Please choose a different time or doctor."
            );
        }

        if (serviceInv.isExistAppointmentForDoctorAt(doctorProfileId, appointmentDate, null)) {
            throw new BadRequestException(
                    "Doctor already has an appointment at the selected time. Please choose a different time or doctor."
            );
        }

        DoctorProfile doctorProfile = (DoctorProfile) doctorProfileServiceInv
                .retrieve(doctorProfileId, null)
                .getBody();

        PatientProfile patientProfile = (PatientProfile) patientProfileServiceInv
                .retrieve(patientProfileId, null)
                .getBody();

        Specialty specialty = doctorProfile != null ? doctorProfile.getSpecialty() : null;

        Appointment appointment = new Appointment();
        appointment.setAppointmentCode(CommonService.generateAppointmentCode());
        appointment.setDoctorProfile(doctorProfile);
        appointment.setPatientProfile(patientProfile);
        appointment.setSpecialty(specialty);
        appointment.setAppointmentDate(appointmentDate);
        appointment.setBookingType(requestBody.getBookingType());
        appointment.setReason(requestBody.getReason());
        appointment.setSymptoms(requestBody.getSymptoms());
        appointment.setNotes(requestBody.getNotes());
        appointment.setStatus(PENDING);

        Appointment saved = (Appointment) serviceInv.create(appointment).getBody();

        return ResponseEntity.ok(saved);
    }

    @Override
    public ResponseEntity<?> update(String id, UpdateAppointmentDto requestBody) {
        Role.RoleName actorRole = resolveActorRole();

        ResponseAppointmentDto appointment = serviceInv.retrieve(id, null).getBody()
                instanceof ResponseAppointmentDto a ? a : null;

        if (appointment == null) {
            throw new BadRequestException("Appointment with ID " + id + " not found");
        }

        Appointment.AppointmentStatus currentStatus = appointment.getStatus();

        Appointment.AppointmentStatus targetStatus = requestBody.getStatus() != null
                ? requestBody.getStatus()
                : currentStatus;

        AppointmentStatusTransition.validate(currentStatus, targetStatus, actorRole);

        boolean isReactivation = currentStatus == CANCELLED && targetStatus == PENDING;

        boolean canEditDetails = currentStatus == PENDING || isReactivation;

        if (!canEditDetails && hasDetailChanges(requestBody)) {
            throw new BadRequestException(String.format(
                    "Cannot change appointment details when status is %s. " +
                            "Only status transition is allowed.", currentStatus));
        }


        String targetDoctorId = (canEditDetails && requestBody.getDoctorProfileId() != null)
                ? requestBody.getDoctorProfileId()
                : appointment.getDoctorProfileId();

        Date targetDate = (canEditDetails && requestBody.getAppointmentDate() != null)
                ? requestBody.getAppointmentDate()
                : appointment.getAppointmentDate();

        boolean doctorChanged = canEditDetails && requestBody.getDoctorProfileId() != null;
        boolean dateChanged = canEditDetails && requestBody.getAppointmentDate() != null;


        // ── 4. Check doctor availability ─────────────────────────────────────────
        // Trigger khi đổi doctor/date, hoặc reactivate (slot cũ có thể đã bị chiếm)
        if (doctorChanged || dateChanged || isReactivation) {
            if (!doctorScheduleExceptionServiceInv.isDoctorAvailable(targetDoctorId, targetDate)) {
                throw new BadRequestException(
                        "Doctor is not available at the selected time. Please choose a different time or doctor.");
            }
            if (serviceInv.isExistAppointmentForDoctorAt(targetDoctorId, targetDate, id)) {
                throw new BadRequestException(
                        "Doctor already has an appointment at the selected time. Please choose a different time or doctor.");
            }
        }

        Appointment appointmentEntity = appointmentRepository
                .findById(UUID.fromString(id))
                .orElseThrow(() -> new BadRequestException("Appointment with ID " + id + " not found"));

        if (doctorChanged) {
            DoctorProfile newDoctor = (DoctorProfile) doctorProfileServiceInv
                    .retrieve(targetDoctorId, null)
                    .getBody();
            appointmentEntity.setDoctorProfile(newDoctor);
            appointmentEntity.setSpecialty(newDoctor != null
                    ? newDoctor.getSpecialty()
                    : appointmentEntity.getSpecialty());
        }

        appointmentEntity.setStatus(targetStatus);
        appointmentEntity.setAppointmentDate(targetDate);

        if (canEditDetails) {
            appointmentEntity.setBookingType(requestBody.getBookingType());
            appointmentEntity.setReason(requestBody.getReason());
            appointmentEntity.setSymptoms(requestBody.getSymptoms());
            appointmentEntity.setNotes(requestBody.getNotes());
        }

        Appointment saved = (Appointment) serviceInv.update(id, appointmentEntity, null).getBody();
        assert saved != null;
        return ResponseEntity.ok(ResponseAppointmentDto.from(saved));
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

    private Role.RoleName resolveActorRole() {
        List<Role.RoleName> roles = SecuritiesUtils.getAuthorities();

        // Ưu tiên ADMIN vì ADMIN có nhiều roles trong authorities
        if (roles.contains(Role.RoleName.ADMIN)) return Role.RoleName.ADMIN;

        return roles.stream()
                .map(r -> {
                    try {
                        return Role.RoleName.valueOf(r.name());
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Cannot determine actor role"));
    }

    private boolean hasDetailChanges(UpdateAppointmentDto dto) {
        return dto.getDoctorProfileId() != null
                || dto.getAppointmentDate() != null
                || dto.getBookingType() != null
                || dto.getReason() != null
                || dto.getSymptoms() != null
                || dto.getNotes() != null;
    }
}
