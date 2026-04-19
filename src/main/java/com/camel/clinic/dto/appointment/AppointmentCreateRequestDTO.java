package com.camel.clinic.dto.appointment;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class AppointmentCreateRequestDTO {
    @NotNull(message = "doctorId is required")
    private UUID doctorId;

    @NotNull(message = "serviceId is required")
    private UUID serviceId;

    @NotNull(message = "date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy", timezone = "Asia/Ho_Chi_Minh")
    private Date date;

    @NotNull(message = "time is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Ho_Chi_Minh")
    private Date time;

    private String reason;
    private String symptoms;

    @NotBlank(message = "serviceType is required")
    private String serviceType;
}

