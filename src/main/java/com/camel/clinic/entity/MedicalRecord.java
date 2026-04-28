package com.camel.clinic.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Date;
import java.util.Map;

@Entity
@Table(name = "medical_record", indexes = {
        @Index(name = "idx_record_code", columnList = "record_code"),
        @Index(name = "idx_appointment_id", columnList = "appointment_id"),
        @Index(name = "idx_patient_id", columnList = "patient_id"),
        @Index(name = "idx_doctor_id", columnList = "doctor_id"),
        // Composite indexes for common queries
        @Index(name = "idx_patient_created", columnList = "patient_id, created_at"),
        @Index(name = "idx_doctor_created", columnList = "doctor_id, created_at")
})
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class MedicalRecord extends SoftDeletableEntity {

    @NotBlank()
    @Column(name = "record_code", unique = true, nullable = false, length = 20)
    private String recordCode;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", unique = true, nullable = false, foreignKey = @ForeignKey(name = "fk_record_appointment"))
    @NotNull()
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false, foreignKey = @ForeignKey(name = "fk_record_patient"))
    @NotNull()
    private PatientProfile patientProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false, foreignKey = @ForeignKey(name = "fk_record_doctor"))
    @NotNull()
    private DoctorProfile doctorProfile;

    @Column(name = "chief_complaint", columnDefinition = "TEXT")
    private String chiefComplaint;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "vital_signs", columnDefinition = "JSON")
    private Map<String, Object> vitalSigns;

    @NotBlank()
    @Column(nullable = false, columnDefinition = "TEXT")
    private String diagnosis;

    @Column(name = "treatment_plan", columnDefinition = "TEXT")
    private String treatmentPlan;

    @Column(name = "follow_up_date")
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "dd/MM/yyyy",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date followUpDate;

    @Column(name = "doctor_notes", columnDefinition = "TEXT")
    private String doctorNotes;
}
