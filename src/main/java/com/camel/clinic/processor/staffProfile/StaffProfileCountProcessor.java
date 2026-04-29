package com.camel.clinic.processor.staffProfile;

import com.camel.clinic.service.doctorProfile.DoctorProfileServiceImp;
import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("staffProfileCountProcessor")
@AllArgsConstructor
public class StaffProfileCountProcessor implements Processor {
    private final DoctorProfileServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        ResponseEntity<?> response = serviceImp.count();
        exchange.getMessage().setBody(response);
    }
}
