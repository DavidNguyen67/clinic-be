package com.camel.clinic.dto.promotion;

import com.camel.clinic.entity.Promotion.DiscountType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class UpdatePromotionDto {

    @Size(max = 50, message = "Code must not exceed 50 characters")
    private String code;

    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    private String description;

    private DiscountType discountType;

    @DecimalMin(value = "0.0", inclusive = false, message = "Discount value must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Discount value format is invalid")
    private BigDecimal discountValue;

    @DecimalMin(value = "0.0", message = "Min purchase amount must be >= 0")
    @Digits(integer = 8, fraction = 2, message = "Min purchase amount format is invalid")
    private BigDecimal minPurchaseAmount;

    @DecimalMin(value = "0.0", inclusive = false, message = "Max discount amount must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Max discount amount format is invalid")
    private BigDecimal maxDiscountAmount;

    @Min(value = 1, message = "Usage limit must be at least 1")
    private Integer usageLimit;

    @Min(value = 1, message = "Usage per user must be at least 1")
    private Integer usagePerUser;

    private List<String> applicableServices;

    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "dd/MM/yyyy",
            timezone = "Asia/Ho_Chi_Minh"
    )
    @Future(message = "Start date must be in the future")
    private Date startDate;

    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "dd/MM/yyyy",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date endDate;

    private Boolean isActive;

    @AssertTrue(message = "End date must be after start date")
    @JsonIgnore
    public boolean isEndDateAfterStartDate() {
        if (startDate == null || endDate == null) return true;
        return endDate.after(startDate);
    }
}