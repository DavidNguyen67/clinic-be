package com.camel.clinic.processor.specialty;

import com.camel.clinic.service.specialty.SpecialtyServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("specialtyListProcessor")
@AllArgsConstructor
@Slf4j
public class SpecialtyListProcessor implements Processor {
    private final SpecialtyServiceImp specialtyServiceImp;

    @Override
    public void process(Exchange exchange) {
        Map<String, Object> queryParams = exchange.getIn().getHeaders();

        ResponseEntity<?> response = specialtyServiceImp.getAllSpecialties(queryParams);

        exchange.getMessage().setBody(response);
    }
}