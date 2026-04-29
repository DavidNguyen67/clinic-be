package com.camel.clinic.processor.doctorProfile;

import com.camel.clinic.service.doctorProfile.DoctorProfileServiceImp;
import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("doctorProfileRestoreProcessor")
@AllArgsConstructor
public class DoctorProfileRestoreProcessor implements Processor {
    private final DoctorProfileServiceImp doctorProfileServiceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        String id = exchange.getIn().getHeader("id", String.class);
        ResponseEntity<?> response = doctorProfileServiceImp.restore(id);
        exchange.getMessage().setBody(response);
    }
}
