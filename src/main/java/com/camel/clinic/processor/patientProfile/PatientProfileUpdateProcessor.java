package com.camel.clinic.processor.patientProfile;

import com.camel.clinic.dto.patientProfile.UpdatePatientProfileDto;
import com.camel.clinic.service.patientProfile.PatientProfileServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("patientProfileUpdateProcessor")
@AllArgsConstructor
@Slf4j
public class PatientProfileUpdateProcessor implements Processor {
    private final PatientProfileServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        UpdatePatientProfileDto request = exchange.getIn().getBody(UpdatePatientProfileDto.class);
        String id = exchange.getIn().getHeader("id", String.class);

        ResponseEntity<?> response = serviceImp.update(id, request);
        exchange.getIn().setBody(response);
    }
}