package com.camel.clinic.processor.appointment;

import com.camel.clinic.dto.appointment.AppointmentCreateRequestDTO;
import com.camel.clinic.service.appointment.AppointmentServiceImp;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component("appointmentCreateProcessor")
@RequiredArgsConstructor
public class AppointmentCreateProcessor implements Processor {

    private final AppointmentServiceImp appointmentServiceImp;

    @Override
    public void process(Exchange exchange) {
        AppointmentCreateRequestDTO request = exchange.getIn().getBody(AppointmentCreateRequestDTO.class);

        exchange.getMessage().setBody(appointmentServiceImp.createAppointment(request));
    }
}

