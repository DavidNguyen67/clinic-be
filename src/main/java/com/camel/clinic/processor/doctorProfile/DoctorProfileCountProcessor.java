package com.camel.clinic.processor.doctorProfile;

import com.camel.clinic.service.doctorProfile.DoctorProfileServiceImp;
import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("doctorProfileCountProcessor")
@AllArgsConstructor
public class DoctorProfileCountProcessor implements Processor {
    private final DoctorProfileServiceImp doctorProfileServiceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        ResponseEntity<?> response = doctorProfileServiceImp.count();
        exchange.getMessage().setBody(response);
    }
}
