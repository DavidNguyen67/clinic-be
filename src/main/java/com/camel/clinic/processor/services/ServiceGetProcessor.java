package com.camel.clinic.processor.services;

import com.camel.clinic.service.services.ServicesServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("serviceGetProcessor")
@AllArgsConstructor
@Slf4j
public class ServiceGetProcessor implements Processor {
    private final ServicesServiceImp servicesServiceImp;

    @Override
    public void process(Exchange exchange) {
        String id = exchange.getIn().getHeader("id", String.class);
        ResponseEntity<?> response = servicesServiceImp.getServiceById(id);
        exchange.getIn().setBody(response);
    }
}