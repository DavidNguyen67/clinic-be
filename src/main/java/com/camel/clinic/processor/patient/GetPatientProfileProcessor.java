package com.camel.clinic.processor.patient;

import com.camel.clinic.service.patient.PatientProfileServiceImp;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component("getPatientProfileProcessor")
@RequiredArgsConstructor
public class GetPatientProfileProcessor implements Processor {

    private final PatientProfileServiceImp patientProfileServiceImp;

    @Override
    public void process(Exchange exchange) {
        exchange.getMessage().setBody(patientProfileServiceImp.getProfile());
    }
}

