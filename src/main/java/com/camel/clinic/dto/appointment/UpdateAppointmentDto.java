package com.camel.clinic.dto.appointment;

import com.camel.clinic.entity.Appointment.AppointmentStatus;
import com.camel.clinic.entity.Appointment.BookingType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateAppointmentDto {
    private String doctorProfileId;

    private String appointmentDate;

    private AppointmentStatus status;

    private BookingType bookingType;

    private String reason;

    private String symptoms;

    private String notes;

    private Integer queueNumber;
}