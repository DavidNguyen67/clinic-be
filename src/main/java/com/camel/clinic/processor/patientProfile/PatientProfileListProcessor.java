package com.camel.clinic.processor.patientProfile;

import com.camel.clinic.service.patientProfile.PatientProfileServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("patientProfileListProcessor")
@AllArgsConstructor
@Slf4j
public class PatientProfileListProcessor implements Processor {
    private final PatientProfileServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) {
        Map<String, Object> queryParams = exchange.getIn().getHeaders();

        ResponseEntity<?> response = serviceImp.list(queryParams);

        exchange.getMessage().setBody(response);
    }
}