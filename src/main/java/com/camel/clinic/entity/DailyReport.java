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
@Table(name = "daily_reports", uniqueConstraints = @UniqueConstraint(name = "unique_report_date", columnNames = "report_date"), indexes = {
        @Index(name = "idx_date", columnList = "report_date")
})
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class DailyReport extends SoftDeletableEntity {

    @NotNull()
    @Column(name = "report_date", unique = true, nullable = false)
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "dd/MM/yyyy",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date reportDate;

    @Column(name = "total_appointments", nullable = false)
    private Integer totalAppointments = 0;

    @Column(name = "completed_appointments", nullable = false)
    private Integer completedAppointments = 0;

    @Column(name = "cancelled_appointments", nullable = false)
    private Integer cancelledAppointments = 0;

    @Column(name = "new_patients", nullable = false)
    private Integer newPatients = 0;

    @Column(name = "returning_patients", nullable = false)
    private Integer returningPatients = 0;

    @Column(name = "total_revenue", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalRevenue = BigDecimal.ZERO;

    @Column(name = "cash_revenue", nullable = false, precision = 12, scale = 2)
    private BigDecimal cashRevenue = BigDecimal.ZERO;

    @Column(name = "online_revenue", nullable = false, precision = 12, scale = 2)
    private BigDecimal onlineRevenue = BigDecimal.ZERO;

    @Column(name = "insurance_revenue", nullable = false, precision = 12, scale = 2)
    private BigDecimal insuranceRevenue = BigDecimal.ZERO;

    @Column(name = "pending_payments", nullable = false, precision = 12, scale = 2)
    private BigDecimal pendingPayments = BigDecimal.ZERO;
}
