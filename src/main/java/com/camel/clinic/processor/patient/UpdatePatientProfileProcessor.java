package com.camel.clinic.processor.patient;

import com.camel.clinic.service.patient.PatientProfileServiceImp;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("updatePatientProfileProcessor")
@RequiredArgsConstructor
public class UpdatePatientProfileProcessor implements Processor {

    private final PatientProfileServiceImp patientProfileServiceImp;

    @Override
    public void process(Exchange exchange) {
        Map<String, Object> requestBody = exchange.getIn().getBody(Map.class);
        exchange.getMessage().setBody(patientProfileServiceImp.updateProfile(requestBody));
    }
}

