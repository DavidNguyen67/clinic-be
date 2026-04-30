package com.camel.clinic.processor.appointment;

import com.camel.clinic.dto.appointment.CreateAppointmentDto;
import com.camel.clinic.service.appointment.AppointmentServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("appointmentCreateProcessor")
@AllArgsConstructor
@Slf4j
public class AppointmentCreateProcessor implements Processor {
    private final AppointmentServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        CreateAppointmentDto request = exchange.getIn().getBody(CreateAppointmentDto.class);
        ResponseEntity<?> response = serviceImp.create(request);
        exchange.getIn().setBody(response);
    }
}