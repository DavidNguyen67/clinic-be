package com.camel.clinic.dto.appointment;

import com.camel.clinic.entity.Appointment;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponseDto {
    private String id;
    private String appointmentCode;

    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "HH:mm:ss dd/MM/yyyy",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date appointmentDate;

    private Appointment.AppointmentStatus status;
    private Appointment.BookingType bookingType;
    private String reason;
    private String symptoms;
    private String notes;
    private Integer queueNumber;

    private String patientProfileId;
    private String patientName;

    private String doctorProfileId;
    private String doctorName;

    private String specialtyId;
    private String specialtyName;

    private String clinicServiceId;
    private String clinicServiceName;

    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "HH:mm:ss dd/MM/yyyy",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date createdAt;

    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "HH:mm:ss dd/MM/yyyy",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date updatedAt;

    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "HH:mm:ss dd/MM/yyyy",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date deletedAt;

    public static AppointmentResponseDto from(Appointment a) {
        AppointmentResponseDto res = new AppointmentResponseDto();
        res.setId(a.getId().toString());
        res.setAppointmentCode(a.getAppointmentCode());
        res.setAppointmentDate(a.getAppointmentDate());
        res.setStatus(a.getStatus());
        res.setBookingType(a.getBookingType());
        res.setReason(a.getReason());
        res.setSymptoms(a.getSymptoms());
        res.setNotes(a.getNotes());
        res.setQueueNumber(a.getQueueNumber());
        res.setCreatedAt(a.getCreatedAt());
        res.setUpdatedAt(a.getUpdatedAt());
        res.setDeletedAt(a.getDeletedAt());

        if (a.getPatientProfile() != null) {
            res.setPatientProfileId(a.getPatientProfile().getId().toString());
            res.setPatientName(a.getPatientProfile().getUser().getFullName());
        }

        if (a.getDoctorProfile() != null) {
            res.setDoctorProfileId(a.getDoctorProfile().getId().toString());
            res.setDoctorName(a.getDoctorProfile().getUser().getFullName());
        }

        if (a.getSpecialty() != null) {
            res.setSpecialtyId(a.getSpecialty().getId().toString());
            res.setSpecialtyName(a.getSpecialty().getName());
        }

        if (a.getClinicService() != null) {
            res.setClinicServiceId(a.getClinicService().getId().toString());
            res.setClinicServiceName(a.getClinicService().getName());
        }

        return res;
    }
}