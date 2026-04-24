package com.camel.clinic.processor.clinicservice;

import com.camel.clinic.service.clinicService.ClinicServiceImp;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("getAllServiceProcessor")
@RequiredArgsConstructor
public class GetAllServiceProcessor implements Processor {

    private final ClinicServiceImp clinicServiceImp;

    @Override
    public void process(Exchange exchange) {
        // Query params (page, size, sortBy, sortDir) are propagated as headers in Camel REST.
        Map<String, Object> queryParams = exchange.getIn().getHeaders();
        ResponseEntity<?> response = clinicServiceImp.list(queryParams);
        exchange.getMessage().setBody(response);
    }
}

