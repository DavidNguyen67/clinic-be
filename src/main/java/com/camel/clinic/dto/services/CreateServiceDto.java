package com.camel.clinic.dto.services;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateServiceDto {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Slug is required")
    @Size(max = 120, message = "Slug must not exceed 120 characters")
    @Pattern(
            regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$",
            message = "Slug must be lowercase alphanumeric with hyphens only (e.g. 'nhi-khoa')"
    )
    private String slug;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    @Pattern(
            regexp = "^(https?:\\/\\/.*)$",
            message = "Image must be a valid URL"
    )
    private String image;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    @DecimalMin(value = "0.0", inclusive = false, message = "Promotional price must be greater than 0")
    private BigDecimal promotionalPrice;

    @Min(value = 5, message = "Duration must be at least 5 minutes")
    @Max(value = 480, message = "Duration must not exceed 480 minutes")
    private Integer duration = 30;

    private Boolean isActive = true;

    private Boolean isFeatured = false;

    @NotBlank(message = "Specialty ID is required")
    private String specialtyId;

    @AssertTrue(message = "Promotional price must be less than price")
    public boolean isValidPromotion() {
        if (price == null || promotionalPrice == null) return true;
        return promotionalPrice.compareTo(price) < 0;
    }
}