package com.camel.clinic.processor.staffProfile;

import com.camel.clinic.service.staffProfile.StaffProfileServiceImp;
import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("staffProfileRestoreProcessor")
@AllArgsConstructor
public class StaffProfileRestoreProcessor implements Processor {
    private final StaffProfileServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        String id = exchange.getIn().getHeader("id", String.class);
        ResponseEntity<?> response = serviceImp.restore(id);
        exchange.getMessage().setBody(response);
    }
}
