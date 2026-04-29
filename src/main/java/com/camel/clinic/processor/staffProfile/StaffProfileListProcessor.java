package com.camel.clinic.processor.staffProfile;

import com.camel.clinic.service.staffProfile.StaffProfileServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("staffProfileListProcessor")
@AllArgsConstructor
@Slf4j
public class StaffProfileListProcessor implements Processor {
    private final StaffProfileServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) {
        Map<String, Object> queryParams = exchange.getIn().getHeaders();

        ResponseEntity<?> response = serviceImp.list(queryParams);

        exchange.getMessage().setBody(response);
    }
}