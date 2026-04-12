package com.camel.clinic.processor.specialty;

import com.camel.clinic.service.specialty.SpecialtyServiceImp;
import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("getAllSpecialtiesProcessor")
@AllArgsConstructor
public class GetAllSpecialtiesProcessor implements Processor {
    private final SpecialtyServiceImp specialtyServiceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        Map<String, Object> queryParams = exchange.getIn().getHeaders();

        ResponseEntity<?> response = specialtyServiceImp.getAllSpecialties(queryParams);

        exchange.getMessage().setBody(response);
    }
}
