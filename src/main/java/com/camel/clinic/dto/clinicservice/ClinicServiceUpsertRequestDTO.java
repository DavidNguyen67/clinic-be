package com.camel.clinic.dto.clinicservice;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ClinicServiceUpsertRequestDTO {

    private UUID specialtyId;

    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "slug is required")
    private String slug;

    private String description;

    @NotNull(message = "price is required")
    @DecimalMin(value = "0.00", message = "price must be >= 0")
    private BigDecimal price;

    @DecimalMin(value = "0.00", message = "promotionalPrice must be >= 0")
    private BigDecimal promotionalPrice;

    @NotNull(message = "duration is required")
    @Min(value = 1, message = "duration must be >= 1")
    private Integer duration;

    private String image;

    @NotNull(message = "isFeatured is required")
    private Boolean isFeatured;

    @NotNull(message = "isActive is required")
    private Boolean isActive;
}

