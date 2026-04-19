package com.camel.clinic.processor.appointment;

import com.camel.clinic.service.appointment.AppointmentServiceImp;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component("appointmentQueueProcessor")
@RequiredArgsConstructor
public class AppointmentQueueProcessor implements Processor {

    private final AppointmentServiceImp appointmentServiceImp;

    @Override
    public void process(Exchange exchange) {
        exchange.getMessage().setBody(appointmentServiceImp.getQueueAppointments());
    }
}

