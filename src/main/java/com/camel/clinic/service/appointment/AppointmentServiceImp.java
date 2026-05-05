package com.camel.clinic.service.appointment;

import com.camel.clinic.dto.appointment.CreateAppointmentDto;
import com.camel.clinic.dto.appointment.UpdateAppointmentDto;
import com.camel.clinic.entity.Appointment;
import com.camel.clinic.entity.DoctorProfile;
import com.camel.clinic.entity.PatientProfile;
import com.camel.clinic.entity.Specialty;
import com.camel.clinic.exception.BadRequestException;
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

@Service
@Slf4j
@AllArgsConstructor
public class AppointmentServiceImp implements AppointmentService {
    private final AppointmentServiceInv serviceInv;
    private final DoctorScheduleExceptionServiceInv doctorScheduleExceptionServiceInv;
    private final DoctorProfileServiceInv doctorProfileServiceInv;
    private final PatientProfileServiceInv patientProfileServiceInv;

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
        Appointment appointment = serviceInv.retrieve(id, null).getBody() instanceof Appointment a ? a : null;

        if (appointment == null) {
            throw new BadRequestException("Appointment with ID " + id + " not found");
        }

        if (appointment.getStatus() != Appointment.AppointmentStatus.PENDING) {
            throw new BadRequestException("Only PENDING appointments can be updated");
        }

        String targetDoctorId = requestBody.getDoctorProfileId() != null
                ? requestBody.getDoctorProfileId()
                : appointment.getDoctorProfile().getId().toString();

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

        if (doctorChanged) {
            DoctorProfile newDoctor = (DoctorProfile) doctorProfileServiceInv
                    .retrieve(targetDoctorId, null)
                    .getBody();
            appointment.setDoctorProfile(newDoctor);
            appointment.setSpecialty(newDoctor != null ? newDoctor.getSpecialty() : appointment.getSpecialty());
        }

        appointment.setAppointmentDate(targetDate);

        appointment.setStatus(requestBody.getStatus());
        appointment.setBookingType(requestBody.getBookingType());
        appointment.setReason(requestBody.getReason());
        appointment.setSymptoms(requestBody.getSymptoms());
        appointment.setNotes(requestBody.getNotes());

        Appointment saved = (Appointment) serviceInv.update(id, appointment, null).getBody();
        return ResponseEntity.ok(saved);
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
