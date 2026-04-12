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
@Table(name = "inventory", indexes = {
        @Index(name = "idx_medication_id", columnList = "medication_id"),
        @Index(name = "idx_batch_number", columnList = "batch_number"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_expiry_date", columnList = "expiry_date")
})
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Inventory extends SoftDeletableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medication_id", nullable = false, foreignKey = @ForeignKey(name = "fk_inventory_medication"))
    @NotNull()
    private Medication medication;

    @Column(name = "batch_number", length = 100)
    private String batchNumber;

    @NotNull()
    @Column(nullable = false)
    private Integer quantity = 0;

    @Column(name = "expiry_date")
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "dd/MM/yyyy",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date expiryDate;

    @Column(length = 255)
    private String supplier;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InventoryStatus status = InventoryStatus.in_stock;

    @Column(name = "alert_threshold", nullable = false)
    private Integer alertThreshold = 10;

    public enum InventoryStatus {
        in_stock, low_stock, out_of_stock, expired
    }

    // Helper method to update status based on quantity and expiry
    public void updateStatus() {
        if (expiryDate != null && expiryDate.before(new Date())) {
            this.status = InventoryStatus.expired;
        } else if (quantity == 0) {
            this.status = InventoryStatus.out_of_stock;
        } else if (quantity <= alertThreshold) {
            this.status = InventoryStatus.low_stock;
        } else {
            this.status = InventoryStatus.in_stock;
        }
    }
}
