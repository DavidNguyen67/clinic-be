package com.camel.clinic.processor.appointment;

import com.camel.clinic.service.appointment.AppointmentServiceImp;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("appointmentCancelProcessor")
@RequiredArgsConstructor
public class AppointmentCancelProcessor implements Processor {

    private final AppointmentServiceImp appointmentServiceImp;

    @Override
    public void process(Exchange exchange) {
        String id = exchange.getIn().getHeader("id", String.class);
        Map<String, Object> requestBody = exchange.getIn().getBody(Map.class);
        exchange.getMessage().setBody(appointmentServiceImp.cancelAppointment(id, requestBody));
    }
}

