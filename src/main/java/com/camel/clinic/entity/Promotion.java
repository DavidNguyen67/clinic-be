package com.camel.clinic.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "promotion", indexes = {
        @Index(name = "idx_code", columnList = "code"),
        @Index(name = "idx_dates", columnList = "start_date, end_date"),
        @Index(name = "idx_active", columnList = "is_active")
})
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Promotion extends SoftDeletableEntity {

    @NotBlank()
    @Column(unique = true, nullable = false, length = 50)
    private String code;

    @NotBlank()
    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, length = 20)
    @NotNull()
    private DiscountType discountType;

    @NotNull()
    @Column(name = "discount_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "min_purchase_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal minPurchaseAmount = BigDecimal.ZERO;

    @Column(name = "max_discount_amount", precision = 10, scale = 2)
    private BigDecimal maxDiscountAmount;

    @Column(name = "usage_limit")
    private Integer usageLimit;

    @Column(name = "usage_count", nullable = false)
    private Integer usageCount = 0;

    @Column(name = "usage_per_user", nullable = false)
    private Integer usagePerUser = 1;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "applicable_services", columnDefinition = "JSON")
    private List<String> applicableServices;

    @NotNull()
    @Column(name = "start_date", nullable = false)
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "dd/MM/yyyy",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date startDate;

    @NotNull()
    @Column(name = "end_date", nullable = false)
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "dd/MM/yyyy",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date endDate;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    public boolean isValid() {
        Date now = new Date();
        return this.isActive && now.after(this.startDate) && now.before(this.endDate) &&
                (this.usageLimit == null || this.usageCount < this.usageLimit);
    }

    public void incrementUsage() {
        this.usageCount++;
    }

    public enum DiscountType {
        PERCENTAGE,
        FIXED_AMOUNT
    }
}
