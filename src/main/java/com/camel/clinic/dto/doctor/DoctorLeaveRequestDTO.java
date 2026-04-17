package com.camel.clinic.dto.doctor;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorLeaveRequestDTO {

    @NotNull(message = "Leave date is required")
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "dd/MM/yyyy",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date leaveDate;

    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "HH:mm:ss",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date startTime;

    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "HH:mm:ss",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date endTime;

    @NotBlank(message = "Reason is required")
    private String reason;
}

