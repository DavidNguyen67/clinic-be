package com.camel.clinic.processor.services;

import com.camel.clinic.service.services.ServicesServiceImp;
import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("serviceRestoreProcessor")
@AllArgsConstructor
public class ServiceRestoreProcessor implements Processor {
    private final ServicesServiceImp servicesServiceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        String id = exchange.getIn().getHeader("id", String.class);
        ResponseEntity<?> response = servicesServiceImp.restore(id);
        exchange.getMessage().setBody(response);
    }
}
