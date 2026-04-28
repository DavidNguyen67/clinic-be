package com.camel.clinic.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "medical_equipment", indexes = {
        @Index(name = "idx_code", columnList = "equipment_code"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_next_maintenance", columnList = "next_maintenance_date")
})
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class MedicalEquipment extends SoftDeletableEntity {

    @NotBlank()
    @Column(name = "equipment_code", unique = true, nullable = false, length = 50)
    private String equipmentCode;

    @NotBlank()
    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 100)
    private String category;

    @Column(length = 255)
    private String manufacturer;

    @Column(length = 100)
    private String model;

    @Column(name = "serial_number", length = 100)
    private String serialNumber;

    @Column(name = "purchase_date")
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "dd/MM/yyyy",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date purchaseDate;

    @Column(name = "purchase_price", precision = 12, scale = 2)
    private BigDecimal purchasePrice;

    @Column(name = "warranty_expiry")
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "dd/MM/yyyy",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date warrantyExpiry;

    @Column(length = 255)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EquipmentStatus status = EquipmentStatus.OPERATIONAL;

    @Column(name = "last_maintenance_date")
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "dd/MM/yyyy",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date lastMaintenanceDate;

    @Column(name = "next_maintenance_date")
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "dd/MM/yyyy",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date nextMaintenanceDate;

    @Column(name = "maintenance_interval_days", nullable = false)
    private Integer maintenanceIntervalDays = 90;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public void updateNextMaintenance() {
        if (lastMaintenanceDate != null) {
            long nextMaintenanceTime = lastMaintenanceDate.getTime() + (long) maintenanceIntervalDays * 24 * 60 * 60 * 1000;
            this.nextMaintenanceDate = new Date(nextMaintenanceTime);
        }
    }


    public enum EquipmentStatus {
        OPERATIONAL,
        UNDER_MAINTENANCE,
        BROKEN,
        RETIRED
    }
}
