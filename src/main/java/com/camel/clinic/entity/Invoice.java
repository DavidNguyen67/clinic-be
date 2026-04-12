package com.camel.clinic.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "invoices", indexes = {
        @Index(name = "idx_invoice_code", columnList = "invoice_code"),
        @Index(name = "idx_appointment_id", columnList = "appointment_id"),
        @Index(name = "idx_patient_id", columnList = "patient_id"),
        @Index(name = "idx_invoice_date", columnList = "invoice_date"),
        @Index(name = "idx_status", columnList = "status"),
        // Composite indexes for common queries
        @Index(name = "idx_patient_status_date", columnList = "patient_id, status, invoice_date"),
        @Index(name = "idx_date_status", columnList = "invoice_date, status"),
        @Index(name = "idx_status_balance", columnList = "status, balance")
})
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Invoice extends SoftDeletableEntity {

    @NotBlank()
    @Column(name = "invoice_code", unique = true, nullable = false, length = 20)
    private String invoiceCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", foreignKey = @ForeignKey(name = "fk_invoice_appointment"))
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false, foreignKey = @ForeignKey(name = "fk_invoice_patient"))
    @NotNull()
    private Patient patient;

    @NotNull()
    @Column(name = "invoice_date", nullable = false)
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "dd/MM/yyyy",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date invoiceDate;

    @NotNull()
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "discount_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @NotNull()
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "insurance_covered", nullable = false, precision = 10, scale = 2)
    private BigDecimal insuranceCovered = BigDecimal.ZERO;

    @Column(name = "patient_paid", nullable = false, precision = 10, scale = 2)
    private BigDecimal patientPaid = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InvoiceStatus status = InvoiceStatus.pending;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("invoice-items")
    private List<InvoiceItem> items = new ArrayList<>();

    public enum InvoiceStatus {
        draft, pending, paid, cancelled
    }

    // Helper methods
    public void addItem(InvoiceItem item) {
        items.add(item);
        item.setInvoice(this);
    }

    public void removeItem(InvoiceItem item) {
        items.remove(item);
        item.setInvoice(null);
    }

    public void calculateTotals() {
        this.subtotal = items.stream()
                .map(InvoiceItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.totalAmount = this.subtotal.subtract(this.discountAmount);
        this.balance = this.totalAmount.subtract(this.insuranceCovered).subtract(this.patientPaid);
    }
}
