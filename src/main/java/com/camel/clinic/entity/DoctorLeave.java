package com.camel.clinic.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "doctor_leave", indexes = {
        @Index(name = "idx_doctor_id", columnList = "doctor_id"),
        @Index(name = "idx_leave_date", columnList = "leave_date"),
        @Index(name = "idx_status", columnList = "status")
})
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class DoctorLeave extends SoftDeletableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false, foreignKey = @ForeignKey(name = "fk_leave_doctor"))
    @NotNull()
    private Doctor doctor;

    @NotNull()
    @Column(name = "leave_date", nullable = false)
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "dd/MM/yyyy",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date leaveDate;

    @Column(name = "start_time")
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "dd/MM/yyyy",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date startTime;

    @Column(name = "end_time")
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "dd/MM/yyyy",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date endTime;

    @Column(length = 255)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LeaveStatus status = LeaveStatus.pending;

    public enum LeaveStatus {
        pending, approved, rejected
    }
}
