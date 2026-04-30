package com.camel.clinic.dto.appointment;

import com.camel.clinic.entity.Appointment.AppointmentStatus;
import com.camel.clinic.entity.Appointment.BookingType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class UpdateAppointmentDto {
    private String doctorProfileId;

    @Future(message = "Appointment date must be in the future")
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "HH:mm:ss dd/MM/yyyy",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date appointmentDate;

    private AppointmentStatus status;

    private BookingType bookingType;

    private String reason;

    private String symptoms;

    private String notes;

    private Integer queueNumber;
}