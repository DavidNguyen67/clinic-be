package com.camel.clinic.processor.services;

import com.camel.clinic.dto.services.UpdateServiceDto;
import com.camel.clinic.service.services.ServicesServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("serviceUpdateProcessor")
@AllArgsConstructor
@Slf4j
public class ServiceUpdateProcessor implements Processor {
    private final ServicesServiceImp servicesServiceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        UpdateServiceDto request = exchange.getIn().getBody(UpdateServiceDto.class);
        String id = exchange.getIn().getHeader("id", String.class);

        ResponseEntity<?> response = servicesServiceImp.update(id, request);
        exchange.getIn().setBody(response);
    }
}