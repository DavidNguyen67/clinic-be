package com.camel.clinic.processor.doctor;

import com.camel.clinic.service.doctor.DoctorServiceImp;
import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component("createDoctorProcessor")
@AllArgsConstructor
public class CreateDoctorProcessor implements Processor {
    private final DoctorServiceImp doctorServiceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        Object body = exchange.getIn().getBody(String.class);

//        ResponseEntity<?> response = doctorServiceImp.createDoctor(body);
        exchange.getMessage().setBody(body);
    }
}
