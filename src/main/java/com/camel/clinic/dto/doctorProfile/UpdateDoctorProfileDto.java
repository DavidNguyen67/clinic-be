package com.camel.clinic.dto.doctorProfile;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UpdateDoctorProfileDto {

    private String userId;


    private String specialtyId;

    @Size(max = 100, message = "Degree must not exceed 100 characters")
    private String degree;

    @Min(value = 0, message = "Experience years must be >= 0")
    @Max(value = 100, message = "Experience years must be <= 100")
    private Integer experienceYears;

    @Size(max = 2000, message = "Education must not exceed 2000 characters")
    private String education;

    @Size(max = 2000, message = "Bio must not exceed 2000 characters")
    private String bio;

    @DecimalMin(value = "0.0", inclusive = true, message = "Consultation fee must be >= 0")
    private BigDecimal consultationFee;

    private Boolean isFeatured;
}