package com.camel.clinic.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

@Entity
@Table(name = "doctor_schedule_exception", indexes = {
        @Index(name = "idx_doctor_id", columnList = "doctor_id"),
        @Index(name = "idx_exception_date", columnList = "exception_date"),
        @Index(name = "idx_doctor_date", columnList = "doctor_id, exception_date")
})
public class DoctorScheduleException extends SoftDeletableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private DoctorProfile doctorProfile;

    @NotNull
    @Column(name = "exception_date", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy", timezone = "Asia/Ho_Chi_Minh")
    private Date exceptionDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ExceptionType type;

    @Column(columnDefinition = "TEXT")
    private String reason;

    public enum ExceptionType {
        LEAVE,  // nghỉ phép (T2-T6)
        EXTRA   // làm thêm (T7, CN)
    }
}