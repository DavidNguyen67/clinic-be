package com.camel.clinic.processor.patientProfile;

import com.camel.clinic.dto.patientProfile.CreatePatientProfileDto;
import com.camel.clinic.service.patientProfile.PatientProfileServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("patientProfileCreateProcessor")
@AllArgsConstructor
@Slf4j
public class PatientProfileCreateProcessor implements Processor {
    private final PatientProfileServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        CreatePatientProfileDto request = exchange.getIn().getBody(CreatePatientProfileDto.class);
        ResponseEntity<?> response = serviceImp.create(request);
        exchange.getIn().setBody(response);
    }
}