package com.camel.clinic.processor.specialty;

import com.camel.clinic.service.specialty.SpecialtyServiceImp;
import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("specialtyRestoreProcessor")
@AllArgsConstructor
public class SpecialtyRestoreProcessor implements Processor {
    private final SpecialtyServiceImp specialtyServiceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        String id = exchange.getIn().getHeader("id", String.class);
        ResponseEntity<?> response = specialtyServiceImp.restore(id);
        exchange.getMessage().setBody(response);
    }
}
