package com.camel.clinic.processor.specialty;

import com.camel.clinic.service.specialty.SpecialtyServiceImp;
import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("specialtyCountProcessor")
@AllArgsConstructor
public class SpecialtyCountProcessor implements Processor {
    private final SpecialtyServiceImp specialtyServiceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        ResponseEntity<?> response = specialtyServiceImp.countAllSpecialties();
        exchange.getMessage().setBody(response);
    }
}
