package com.camel.clinic.dto.appointment;

import com.camel.clinic.entity.Appointment.BookingType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class CreateAppointmentDto {
    @NotNull(message = "Patient ID is required")
    private String patientProfileId;

    @NotNull(message = "Doctor ID is required")
    private String doctorProfileId;

    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "HH:mm dd/MM/yyyy",
            timezone = "Asia/Ho_Chi_Minh"
    )
    @Future(message = "Appointment date must be in the future")
    private Date appointmentDate;

    private BookingType bookingType = BookingType.ONLINE;

    private String reason;

    private String symptoms;

    private String notes;

}