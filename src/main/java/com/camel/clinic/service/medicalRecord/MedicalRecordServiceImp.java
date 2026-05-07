package com.camel.clinic.service.medicalRecord;

import com.camel.clinic.dto.medicalRecord.CreateMedicalRecordDto;
import com.camel.clinic.dto.medicalRecord.UpdateMedicalRecordDto;
import com.camel.clinic.entity.Appointment;
import com.camel.clinic.entity.DoctorProfile;
import com.camel.clinic.entity.MedicalRecord;
import com.camel.clinic.entity.PatientProfile;
import com.camel.clinic.exception.BadRequestException;
import com.camel.clinic.service.doctorProfile.DoctorProfileServiceInv;
import com.camel.clinic.service.patientProfile.PatientProfileServiceInv;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class MedicalRecordServiceImp implements MedicalRecordService {
    private final MedicalRecordServiceInv serviceInv;
    private final DoctorProfileServiceInv doctorProfileServiceInv;
    private final PatientProfileServiceInv patientProfileServiceInv;
    private final MedicalRecordServiceInv medicalRecordServiceInv;

    @Override
    public ResponseEntity<?> count() {
        return serviceInv.count();
    }

    @Override
    public ResponseEntity<?> retrieve(String id) {
        return serviceInv.retrieve(id, null);
    }

    @Override
    public ResponseEntity<?> create(CreateMedicalRecordDto requestBody) {
        String doctorProfileId = requestBody.getDoctorProfileId();
        String patientProfileId = requestBody.getPatientProfileId();
        String appointmentId = requestBody.getAppointmentId();

        Appointment appointment = (Appointment) medicalRecordServiceInv
                .retrieve(appointmentId, null)
                .getBody();

        if (appointment == null) {
            throw new BadRequestException("Appointment with ID " + appointmentId + " not found");
        }

        if (appointment.getStatus() != Appointment.AppointmentStatus.COMPLETED) {
            throw new BadRequestException("Cannot create medical record for appointment with status " + appointment.getStatus());
        }

        DoctorProfile doctorProfile = (DoctorProfile) doctorProfileServiceInv
                .retrieve(doctorProfileId, null)
                .getBody();

        if (doctorProfile == null) {
            throw new BadRequestException("Doctor profile with ID " + doctorProfileId + " not found");
        }

        PatientProfile patientProfile = (PatientProfile) patientProfileServiceInv
                .retrieve(patientProfileId, null)
                .getBody();

        if (patientProfile == null) {
            throw new BadRequestException("Patient profile with ID " + patientProfileId + " not found");
        }


        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setDoctorProfile(doctorProfile);
        medicalRecord.setPatientProfile(patientProfile);
        medicalRecord.setAppointment(appointment);
        medicalRecord.setChiefComplaint(requestBody.getChiefComplaint());
        medicalRecord.setVitalSigns(requestBody.getVitalSigns());
        medicalRecord.setDiagnosis(requestBody.getDiagnosis());
        medicalRecord.setTreatmentPlan(requestBody.getTreatmentPlan());
        medicalRecord.setFollowUpDate(requestBody.getFollowUpDate());
        medicalRecord.setDoctorNotes(requestBody.getDoctorNotes());

        return serviceInv.create(medicalRecord);
    }

    @Override
    public ResponseEntity<?> update(String id, UpdateMedicalRecordDto requestBody) {
        MedicalRecord medicalRecord = serviceInv.retrieve(id, null).getBody() instanceof MedicalRecord mc ? mc : null;
        if (medicalRecord == null) {
            throw new IllegalArgumentException("MedicalRecord with ID " + id + " not found");
        }

        Appointment appointment = (Appointment) medicalRecordServiceInv
                .retrieve(requestBody.getAppointmentId(), null).getBody();
        if (appointment == null) {
            throw new BadRequestException("Appointment with ID " + requestBody.getAppointmentId() + " not found");
        }

        if (appointment.getStatus() != Appointment.AppointmentStatus.COMPLETED) {
            throw new BadRequestException("Cannot update medical record for appointment with status " + appointment.getStatus());
        }

        DoctorProfile doctorProfile = (DoctorProfile) doctorProfileServiceInv
                .retrieve(requestBody.getDoctorProfileId(), null).getBody();
        if (doctorProfile == null) {
            throw new BadRequestException("Doctor profile with ID " + requestBody.getDoctorProfileId() + " not found");
        }

        PatientProfile patientProfile = (PatientProfile) patientProfileServiceInv
                .retrieve(requestBody.getPatientProfileId(), null).getBody();
        if (patientProfile == null) {
            throw new BadRequestException("Patient profile with ID " + requestBody.getPatientProfileId() + " not found");
        }


        medicalRecord.setDoctorProfile(doctorProfile);
        medicalRecord.setPatientProfile(patientProfile);
        medicalRecord.setAppointment(appointment);
        medicalRecord.setChiefComplaint(requestBody.getChiefComplaint());
        medicalRecord.setVitalSigns(requestBody.getVitalSigns());
        medicalRecord.setDiagnosis(requestBody.getDiagnosis());
        medicalRecord.setTreatmentPlan(requestBody.getTreatmentPlan());
        medicalRecord.setFollowUpDate(requestBody.getFollowUpDate());
        medicalRecord.setDoctorNotes(requestBody.getDoctorNotes());

        return serviceInv.update(id, medicalRecord, null);
    }

    @Override
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
