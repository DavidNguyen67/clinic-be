package com.camel.clinic.processor.doctorProfile;

import com.camel.clinic.service.doctorProfile.DoctorProfileServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("doctorProfileListProcessor")
@AllArgsConstructor
@Slf4j
public class DoctorProfileListProcessor implements Processor {
    private final DoctorProfileServiceImp doctorProfileServiceImp;

    @Override
    public void process(Exchange exchange) {
        Map<String, Object> queryParams = exchange.getIn().getHeaders();

        ResponseEntity<?> response = doctorProfileServiceImp.list(queryParams);

        exchange.getMessage().setBody(response);
    }
}