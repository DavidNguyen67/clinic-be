package com.camel.clinic.dto.doctorScheduleException;

import com.camel.clinic.entity.DoctorScheduleException;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateDoctorScheduleExceptionDto {

    @Pattern(
            regexp = "^\\d{2}/\\d{2}/\\d{4}$",
            message = "Date must be in format DD/MM/YYYY"
    )
    private String exceptionDate;

    private DoctorScheduleException.ExceptionType type;

    @Size(max = 1000)
    private String reason;
}