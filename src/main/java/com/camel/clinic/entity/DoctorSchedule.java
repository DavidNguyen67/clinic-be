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
@Table(name = "doctor_schedules", indexes = {
        @Index(name = "idx_doctor_id", columnList = "doctor_id"),
        @Index(name = "idx_day_of_week", columnList = "day_of_week"),
        @Index(name = "idx_is_active", columnList = "is_active")
})
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class DoctorSchedule extends SoftDeletableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false, foreignKey = @ForeignKey(name = "fk_schedule_doctor"))
    @NotNull()
    private Doctor doctor;

    @NotNull()
    @Column(name = "day_of_week", nullable = false)
    private Integer dayOfWeek; // 0 = Sunday, 1 = Monday, ..., 6 = Saturday

    @NotNull()
    @Column(name = "start_time", nullable = false)
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "dd/MM/yyyy",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date startTime;

    @NotNull()
    @Column(name = "end_time", nullable = false)
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "dd/MM/yyyy",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date endTime;

    @Column(name = "slot_duration", nullable = false)
    private Integer slotDuration = 30; // minutes

    @Column(name = "max_patients_per_slot", nullable = false)
    private Integer maxPatientsPerSlot = 1;

    @Column(length = 255)
    private String location;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
