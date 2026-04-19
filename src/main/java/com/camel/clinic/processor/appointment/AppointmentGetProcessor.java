package com.camel.clinic.processor.appointment;

import com.camel.clinic.service.appointment.AppointmentServiceImp;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component("appointmentGetProcessor")
@RequiredArgsConstructor
public class AppointmentGetProcessor implements Processor {

    private final AppointmentServiceImp appointmentServiceImp;

    @Override
    public void process(Exchange exchange) {
        String id = exchange.getIn().getHeader("id", String.class);
        exchange.getMessage().setBody(appointmentServiceImp.getAppointmentDetail(id));
    }
}

