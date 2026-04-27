package com.camel.clinic.processor.appointment;

import com.camel.clinic.service.appointment.AppointmentServiceImp;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("staffAppointmentStatsProcessor")
@RequiredArgsConstructor
public class StaffAppointmentStatsProcessor implements Processor {

    private final AppointmentServiceImp appointmentServiceImp;

    @Override
    public void process(Exchange exchange) {
        // Query params from GET requests are mapped to headers in Camel REST DSL.
        Map<String, Object> queryParams = exchange.getIn().getHeaders();

        exchange.getMessage().setBody(appointmentServiceImp.getStaffAppointmentStats(queryParams));
    }
}
