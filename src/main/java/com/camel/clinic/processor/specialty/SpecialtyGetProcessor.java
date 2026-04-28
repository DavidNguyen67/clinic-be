package com.camel.clinic.processor.specialty;

import com.camel.clinic.service.specialty.SpecialtyServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("specialtyGetProcessor")
@AllArgsConstructor
@Slf4j
public class SpecialtyGetProcessor implements Processor {
    private final SpecialtyServiceImp specialtyServiceImp;

    @Override
    public void process(Exchange exchange) {
        String id = exchange.getIn().getHeader("id", String.class);
        ResponseEntity<?> response = specialtyServiceImp.getSpecialtyById(id);
        exchange.getIn().setBody(response);
    }
}