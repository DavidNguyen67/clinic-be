package com.camel.clinic.processor.staffProfile;

import com.camel.clinic.dto.staffProfile.CreateStaffProfileDto;
import com.camel.clinic.service.staffProfile.StaffProfileServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("staffProfileCreateProcessor")
@AllArgsConstructor
@Slf4j
public class StaffProfileCreateProcessor implements Processor {
    private final StaffProfileServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        CreateStaffProfileDto request = exchange.getIn().getBody(CreateStaffProfileDto.class);
        ResponseEntity<?> response = serviceImp.create(request);
        exchange.getIn().setBody(response);
    }
}