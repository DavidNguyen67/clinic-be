package com.camel.clinic.dto.appointment;

import com.camel.clinic.entity.Appointment.BookingType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateAppointmentDto {

    @NotNull(message = "Patient ID is required")
    private String patientProfileId;

    @NotNull(message = "Doctor ID is required")
    private String doctorProfileId;

    @NotNull(message = "Appointment date is required")
    private String appointmentDate;

    private BookingType bookingType = BookingType.ONLINE;

    private String reason;

    private String symptoms;

    private String notes;
}