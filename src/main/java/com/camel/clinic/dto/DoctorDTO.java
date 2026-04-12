package com.camel.clinic.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class DoctorDTO {
    private UUID id;
    private String degree;
    private Integer experienceYears;
    private Integer totalReviews;
    private String education;
    private BigDecimal averageRating;
    private String fullName;
    private String pathAvatar;
}