package com.camel.clinic.dto.promotion;

import com.camel.clinic.entity.Promotion.DiscountType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CreatePromotionDto {
    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    private String description;

    @NotNull(message = "Discount type is required")
    private DiscountType discountType;

    @NotNull(message = "Discount value is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Discount value must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Discount value format is invalid")
    private BigDecimal discountValue;

    @DecimalMin(value = "0.0", message = "Min purchase amount must be >= 0")
    @Digits(integer = 8, fraction = 2, message = "Min purchase amount format is invalid")
    private BigDecimal minPurchaseAmount = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", inclusive = false, message = "Max discount amount must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Max discount amount format is invalid")
    private BigDecimal maxDiscountAmount;

    @Min(value = 1, message = "Usage limit must be at least 1")
    private Integer usageLimit;

    @Min(value = 1, message = "Usage per user must be at least 1")
    private Integer usagePerUser = 1;

    private List<String> applicableServices;

    @NotNull(message = "Start date is required")
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "dd/MM/yyyy",
            timezone = "Asia/Ho_Chi_Minh"
    )
    @Future(message = "Start date must be in the future")
    private Date startDate;

    @NotNull(message = "End date is required")
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "dd/MM/yyyy",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date endDate;

    private Boolean isActive = true;

    @AssertTrue(message = "End date must be after start date")
    public boolean isEndDateAfterStartDate() {
        if (startDate == null || endDate == null) return true;
        return endDate.after(startDate);
    }
}