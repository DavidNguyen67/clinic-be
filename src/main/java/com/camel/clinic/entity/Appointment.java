package com.camel.clinic.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;


@Entity
@Table(name = "appointment", indexes = {
        @Index(name = "idx_appointment_code", columnList = "appointment_code"),
        @Index(name = "idx_patient_id", columnList = "patient_id"),
        @Index(name = "idx_doctor_id", columnList = "doctor_id"),
        @Index(name = "idx_date", columnList = "appointment_date"),
        @Index(name = "idx_status", columnList = "status"),
        // Composite indexes for common queries
        @Index(name = "idx_doctor_date_status", columnList = "doctor_id, appointment_date, status"),
        @Index(name = "idx_patient_status_date", columnList = "patient_id, status, appointment_date"),
        @Index(name = "idx_date_status", columnList = "appointment_date, status")
})
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Appointment extends SoftDeletableEntity {

    @NotBlank
    @Column(name = "appointment_code", unique = true, nullable = false, length = 20)
    private String appointmentCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false, foreignKey = @ForeignKey(name = "fk_appointment_patient"))
    @NotNull
    private PatientProfile patientProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false, foreignKey = @ForeignKey(name = "fk_appointment_doctor"))
    @NotNull
    private DoctorProfile doctorProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialty_id", foreignKey = @ForeignKey(name = "fk_appointment_specialty"))
    private Specialty specialty;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", foreignKey = @ForeignKey(name = "fk_appointment_service"))
    private ClinicService clinicService;

    @NotNull()
    @Column(name = "appointment_date", nullable = false)
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "HH:mm:ss dd/MM/yyyy",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date appointmentDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AppointmentStatus status = AppointmentStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "booking_type", nullable = false, length = 20)
    private BookingType bookingType = BookingType.ONLINE;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(columnDefinition = "TEXT")
    private String symptoms;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "queue_number")
    private Integer queueNumber;

    public enum AppointmentStatus {
        PENDING,
        CONFIRMED,
        CHECKED_IN,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED,
        NO_SHOW
    }

    public enum BookingType {
        ONLINE,
        PHONE,
        WALK_IN
    }
}
