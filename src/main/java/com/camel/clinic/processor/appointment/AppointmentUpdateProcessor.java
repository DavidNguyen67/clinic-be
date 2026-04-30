package com.camel.clinic.processor.appointment;

import com.camel.clinic.dto.appointment.UpdateAppointmentDto;
import com.camel.clinic.service.appointment.AppointmentServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("appointmentUpdateProcessor")
@AllArgsConstructor
@Slf4j
public class AppointmentUpdateProcessor implements Processor {
    private final AppointmentServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        UpdateAppointmentDto request = exchange.getIn().getBody(UpdateAppointmentDto.class);
        String id = exchange.getIn().getHeader("id", String.class);

        ResponseEntity<?> response = serviceImp.update(id, request);
        exchange.getIn().setBody(response);
    }
}