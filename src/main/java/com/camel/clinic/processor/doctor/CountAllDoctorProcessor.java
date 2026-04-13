package com.camel.clinic.processor.doctor;

import com.camel.clinic.service.doctor.DoctorServiceImp;
import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component("countAllDoctorProcessor")
@AllArgsConstructor
public class CountAllDoctorProcessor implements Processor {
    private final DoctorServiceImp doctorServiceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        ResponseEntity<?> response = doctorServiceImp.countAllDoctors();

        exchange.getMessage().setBody(response);

    }
}
