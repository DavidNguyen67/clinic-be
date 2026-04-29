package com.camel.clinic.dto.doctorScheduleException;

import com.camel.clinic.entity.DoctorScheduleException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateDoctorScheduleExceptionDto {

    @NotBlank(message = "Doctor Profile ID is required")
    private String doctorProfileId;

    @NotBlank(message = "Exception date is required")
    @Pattern(
            regexp = "^\\d{2}/\\d{2}/\\d{4}$",
            message = "Date must be in format DD/MM/YYYY"
    )
    private String exceptionDate;

    @NotNull(message = "Exception type is required")
    private DoctorScheduleException.ExceptionType type;

    @Size(max = 1000, message = "Reason must not exceed 1000 characters")
    private String reason;
}