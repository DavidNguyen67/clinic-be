package com.camel.clinic.processor.patient;

import com.camel.clinic.service.patient.PatientProfileServiceImp;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component("getPatientAppointmentsProcessor")
@RequiredArgsConstructor
public class GetPatientAppointmentsProcessor implements Processor {

    private final PatientProfileServiceImp patientProfileServiceImp;

    @Override
    public void process(Exchange exchange) {
        exchange.getMessage().setBody(patientProfileServiceImp.getAllAppointmentByPatientId());
    }
}

