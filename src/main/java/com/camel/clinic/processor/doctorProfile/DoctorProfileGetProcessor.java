package com.camel.clinic.processor.doctorProfile;

import com.camel.clinic.service.doctorProfile.DoctorProfileServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("doctorProfileGetProcessor")
@AllArgsConstructor
@Slf4j
public class DoctorProfileGetProcessor implements Processor {
    private final DoctorProfileServiceImp doctorProfileServiceImp;

    @Override
    public void process(Exchange exchange) {
        String id = exchange.getIn().getHeader("id", String.class);
        ResponseEntity<?> response = doctorProfileServiceImp.retrieve(id);
        exchange.getIn().setBody(response);
    }
}