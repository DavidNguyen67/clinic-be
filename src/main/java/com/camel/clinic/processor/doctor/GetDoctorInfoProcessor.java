package com.camel.clinic.processor.doctor;

import com.camel.clinic.service.doctor.DoctorServiceImp;
import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("getDoctorInfoProcessor")
@AllArgsConstructor
public class GetDoctorInfoProcessor implements Processor {
    private final DoctorServiceImp doctorServiceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        String doctorId = exchange.getIn().getHeader("doctorId", String.class);

        ResponseEntity<?> response = doctorServiceImp.getDoctorInfo(doctorId);

        exchange.getMessage().setBody(response);
    }
}
