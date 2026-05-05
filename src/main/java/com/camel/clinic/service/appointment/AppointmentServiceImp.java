package com.camel.clinic.service.appointment;

import com.camel.clinic.dto.appointment.CreateAppointmentDto;
import com.camel.clinic.dto.appointment.ResponseAppointmentDto;
import com.camel.clinic.dto.appointment.UpdateAppointmentDto;
import com.camel.clinic.entity.Appointment;
import com.camel.clinic.entity.DoctorProfile;
import com.camel.clinic.entity.PatientProfile;
import com.camel.clinic.entity.Specialty;
import com.camel.clinic.exception.BadRequestException;
import com.camel.clinic.repository.AppointmentRepository;
import com.camel.clinic.service.CommonService;
import com.camel.clinic.service.doctorProfile.DoctorProfileServiceInv;
import com.camel.clinic.service.doctorScheduleException.DoctorScheduleExceptionServiceInv;
import com.camel.clinic.service.patientProfile.PatientProfileServiceInv;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

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
        appointment.setStatus(Appointment.AppointmentStatus.PENDING);

        Appointment saved = (Appointment) serviceInv.create(appointment).getBody();

        return ResponseEntity.ok(saved);
    }

    @Override
    public ResponseEntity<?> update(String id, UpdateAppointmentDto requestBody) {
        ResponseAppointmentDto appointment = serviceInv.retrieve(id, null).getBody() instanceof ResponseAppointmentDto a ? a : null;

        if (appointment == null) {
            throw new BadRequestException("Appointment with ID " + id + " not found");
        }

        if (appointment.getStatus() != Appointment.AppointmentStatus.PENDING) {
            throw new BadRequestException("Only PENDING appointments can be updated");
        }

        String targetDoctorId = requestBody.getDoctorProfileId() != null
                ? requestBody.getDoctorProfileId()
                : appointment.getDoctorProfileId();

        Date targetDate = requestBody.getAppointmentDate() != null
                ? requestBody.getAppointmentDate()
                : appointment.getAppointmentDate();

        boolean doctorChanged = requestBody.getDoctorProfileId() != null;
        boolean dateChanged = requestBody.getAppointmentDate() != null;

        if (doctorChanged || dateChanged) {
            if (!doctorScheduleExceptionServiceInv.isDoctorAvailable(targetDoctorId, targetDate)) {
                throw new BadRequestException(
                        "Doctor is not available at the selected time. Please choose a different time or doctor."
                );
            }

            if (serviceInv.isExistAppointmentForDoctorAt(targetDoctorId, targetDate, id)) {
                throw new BadRequestException(
                        "Doctor already has an appointment at the selected time. Please choose a different time or doctor."
                );
            }
        }

        Appointment appointmentEntity = appointmentRepository.findById(UUID.fromString(id)).orElse(null);

        if (doctorChanged) {
            DoctorProfile newDoctor = (DoctorProfile) doctorProfileServiceInv
                    .retrieve(targetDoctorId, null)
                    .getBody();
            assert appointmentEntity != null;
            appointmentEntity.setDoctorProfile(newDoctor);
            appointmentEntity.setSpecialty(newDoctor != null ? newDoctor.getSpecialty() : appointmentEntity.getSpecialty());
        }

        assert appointmentEntity != null;
        appointmentEntity.setAppointmentDate(targetDate);
        appointmentEntity.setStatus(requestBody.getStatus());
        appointmentEntity.setBookingType(requestBody.getBookingType());
        appointmentEntity.setReason(requestBody.getReason());
        appointmentEntity.setSymptoms(requestBody.getSymptoms());
        appointmentEntity.setNotes(requestBody.getNotes());

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
}
