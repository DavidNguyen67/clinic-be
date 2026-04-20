package com.camel.clinic.dto.clinicservice;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Data
@Builder
public class ClinicServiceResponseDTO {
    private UUID id;
    private UUID specialtyId;
    private String specialtyName;
    private String name;
    private String slug;
    private String description;
    private BigDecimal price;
    private BigDecimal promotionalPrice;
    private Integer duration;
    private String image;
    private Boolean isFeatured;
    private Boolean isActive;
    private Date createdAt;
    private Date updatedAt;
}

