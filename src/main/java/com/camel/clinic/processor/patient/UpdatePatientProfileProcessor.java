package com.camel.clinic.processor.patient;

import com.camel.clinic.dto.patient.UpdatePatientProfileDto;
import com.camel.clinic.service.patient.PatientProfileServiceImp;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component("updatePatientProfileProcessor")
@RequiredArgsConstructor
public class UpdatePatientProfileProcessor implements Processor {

    private final PatientProfileServiceImp patientProfileServiceImp;

    @Override
    public void process(Exchange exchange) {
        UpdatePatientProfileDto requestBody = exchange.getIn().getBody(UpdatePatientProfileDto.class);
        exchange.getMessage().setBody(patientProfileServiceImp.updateProfile(requestBody));
    }
}

