package com.camel.clinic.processor.specialty;

import com.camel.clinic.dto.specialty.CreateSpecialtyDto;
import com.camel.clinic.service.specialty.SpecialtyServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("specialtyCreateProcessor")
@AllArgsConstructor
@Slf4j
public class SpecialtyCreateProcessor implements Processor {
    private final SpecialtyServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        CreateSpecialtyDto request = exchange.getIn().getBody(CreateSpecialtyDto.class);
        ResponseEntity<?> response = serviceImp.create(request);
        exchange.getIn().setBody(response);
    }
}