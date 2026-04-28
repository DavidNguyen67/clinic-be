package com.camel.clinic.processor.services;

import com.camel.clinic.service.services.ServicesServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("serviceDeleteProcessor")
@AllArgsConstructor
@Slf4j
public class ServiceDeleteProcessor implements Processor {
    private final ServicesServiceImp servicesServiceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        String id = exchange.getIn().getHeader("id", String.class);

        ResponseEntity<?> response = servicesServiceImp.delete(id);
        exchange.getIn().setBody(response);
    }
}