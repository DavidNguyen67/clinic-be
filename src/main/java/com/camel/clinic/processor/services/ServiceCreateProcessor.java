package com.camel.clinic.processor.services;

import com.camel.clinic.dto.services.CreateServiceDto;
import com.camel.clinic.service.services.ServicesServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("serviceCreateProcessor")
@AllArgsConstructor
@Slf4j
public class ServiceCreateProcessor implements Processor {
    private final ServicesServiceImp servicesServiceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        CreateServiceDto request = exchange.getIn().getBody(CreateServiceDto.class);
        ResponseEntity<?> response = servicesServiceImp.create(request);
        exchange.getIn().setBody(response);
    }
}