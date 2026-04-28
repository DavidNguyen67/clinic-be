package com.camel.clinic.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "payment", indexes = {
        @Index(name = "idx_payment_code", columnList = "payment_code"),
        @Index(name = "idx_invoice_id", columnList = "invoice_id"),
        @Index(name = "idx_patient_id", columnList = "patient_id"),
        @Index(name = "idx_payment_date", columnList = "payment_date"),
        @Index(name = "idx_status", columnList = "status"),
        // Composite indexes for common queries
        @Index(name = "idx_status_date", columnList = "status, payment_date"),
        @Index(name = "idx_patient_status", columnList = "patient_id, status")
})
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Payment extends SoftDeletableEntity {

    @NotBlank()
    @Column(name = "payment_code", unique = true, nullable = false, length = 20)
    private String paymentCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false, foreignKey = @ForeignKey(name = "fk_payment_invoice"))
    @NotNull()
    private Invoice invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false, foreignKey = @ForeignKey(name = "fk_payment_patient"))
    @NotNull()
    private PatientProfile patientProfile;

    @NotNull()
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 20)
    @NotNull()
    private PaymentMethod paymentMethod;

    @Column(name = "payment_date", nullable = false)
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "dd/MM/yyyy",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date paymentDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status = PaymentStatus.PENDING;

    public enum PaymentMethod {
        CASH,
        CARD,
        BANK_TRANSFER,
        MOMO,
        VNPAY
    }

    public enum PaymentStatus {
        PENDING,
        COMPLETED,
        FAILED,
        REFUNDED,
        CANCELLED
    }
}
