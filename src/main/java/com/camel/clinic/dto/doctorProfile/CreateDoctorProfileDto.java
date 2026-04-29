package com.camel.clinic.dto.doctorProfile;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateDoctorProfileDto {
    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Specialty ID is required")
    private String specialtyId;

    @Size(max = 100, message = "Degree must not exceed 100 characters")
    private String degree;

    @NotNull(message = "Experience years is required")
    @Min(value = 0, message = "Experience years must be >= 0")
    @Max(value = 100, message = "Experience years must be <= 100")
    private Integer experienceYears = 0;

    @Size(max = 2000, message = "Education must not exceed 2000 characters")
    private String education;

    @Size(max = 2000, message = "Bio must not exceed 2000 characters")
    private String bio;

    @NotNull(message = "Consultation fee is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Consultation fee must be >= 0")
    private BigDecimal consultationFee = BigDecimal.ZERO;

    private Boolean isFeatured = false;
}