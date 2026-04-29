package com.camel.clinic.processor.patientProfile;

import com.camel.clinic.service.patientProfile.PatientProfileServiceImp;
import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("patientProfileCountProcessor")
@AllArgsConstructor
public class PatientProfileCountProcessor implements Processor {
    private final PatientProfileServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        ResponseEntity<?> response = serviceImp.count();
        exchange.getMessage().setBody(response);
    }
}
