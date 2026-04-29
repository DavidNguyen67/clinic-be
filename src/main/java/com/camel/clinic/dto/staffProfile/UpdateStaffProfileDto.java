package com.camel.clinic.dto.staffProfile;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateStaffProfileDto {
    @Size(max = 100)
    private String position;

    @Size(max = 100)
    private String department;

    @Pattern(
            regexp = "^\\d{2}/\\d{2}/\\d{4}$",
            message = "Hire date must be in format DD/MM/YYYY"
    )
    private String hireDate;
}