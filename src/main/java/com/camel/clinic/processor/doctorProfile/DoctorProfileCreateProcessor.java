package com.camel.clinic.processor.doctorProfile;

import com.camel.clinic.dto.doctorProfile.CreateDoctorProfileDto;
import com.camel.clinic.service.doctorProfile.DoctorProfileServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("doctorProfileCreateProcessor")
@AllArgsConstructor
@Slf4j
public class DoctorProfileCreateProcessor implements Processor {
    private final DoctorProfileServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        CreateDoctorProfileDto request = exchange.getIn().getBody(CreateDoctorProfileDto.class);
        ResponseEntity<?> response = serviceImp.create(request);
        exchange.getIn().setBody(response);
    }
}