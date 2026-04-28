package com.camel.clinic.processor.services;

import com.camel.clinic.service.services.ServicesServiceImp;
import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("serviceCountProcessor")
@AllArgsConstructor
public class ServiceCountProcessor implements Processor {
    private final ServicesServiceImp servicesServiceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        ResponseEntity<?> response = servicesServiceImp.countAllServices();
        exchange.getMessage().setBody(response);
    }
}
