package com.camel.clinic.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "equipment_maintenance", indexes = {
        @Index(name = "idx_equipment", columnList = "equipment_id"),
        @Index(name = "idx_scheduled", columnList = "scheduled_date"),
        @Index(name = "idx_status", columnList = "status")
})
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class EquipmentMaintenance extends SoftDeletableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id", nullable = false, foreignKey = @ForeignKey(name = "fk_maintenance_equipment"))
    @NotNull()
    private MedicalEquipment equipment;

    @Enumerated(EnumType.STRING)
    @Column(name = "maintenance_type", nullable = false, length = 20)
    @NotNull()
    private MaintenanceType maintenanceType;

    @Column(name = "scheduled_date")
    @JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "dd/MM/yyyy",
        timezone = "Asia/Ho_Chi_Minh"
    )
    private Date scheduledDate;

    @Column(name = "completed_date")
    @JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "dd/MM/yyyy",
        timezone = "Asia/Ho_Chi_Minh"
    )
    private Date completedDate;

    @Column(name = "performed_by", length = 255)
    private String performedBy;

    @Column(precision = 10, scale = 2)
    private BigDecimal cost;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "issues_found", columnDefinition = "TEXT")
    private String issuesFound;

    @Column(name = "actions_taken", columnDefinition = "TEXT")
    private String actionsTaken;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MaintenanceStatus status = MaintenanceStatus.scheduled;

    public enum MaintenanceType {
        routine, repair, calibration, inspection
    }

    public enum MaintenanceStatus {
        scheduled, in_progress, completed, cancelled
    }
}
