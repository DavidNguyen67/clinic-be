package com.camel.clinic.processor.patientProfile;

import com.camel.clinic.service.patientProfile.PatientProfileServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("patientProfileGetProcessor")
@AllArgsConstructor
@Slf4j
public class PatientProfileGetProcessor implements Processor {
    private final PatientProfileServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) {
        String id = exchange.getIn().getHeader("id", String.class);
        ResponseEntity<?> response = serviceImp.retrieve(id);
        exchange.getIn().setBody(response);
    }
}