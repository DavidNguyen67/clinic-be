package com.camel.clinic.dto.staffProfile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateStaffProfileDto {
    @NotBlank(message = "User ID is required")
    private String userId;

    @Size(max = 100, message = "Position must not exceed 100 characters")
    private String position;

    @Size(max = 100, message = "Department must not exceed 100 characters")
    private String department;

    @Pattern(
            regexp = "^\\d{2}/\\d{2}/\\d{4}$",
            message = "Hire date must be in format DD/MM/YYYY"
    )
    private String hireDate;
}