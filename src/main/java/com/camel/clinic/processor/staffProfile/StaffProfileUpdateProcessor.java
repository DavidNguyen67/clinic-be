package com.camel.clinic.processor.staffProfile;

import com.camel.clinic.dto.staffProfile.UpdateStaffProfileDto;
import com.camel.clinic.service.staffProfile.StaffProfileServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("staffProfileUpdateProcessor")
@AllArgsConstructor
@Slf4j
public class StaffProfileUpdateProcessor implements Processor {
    private final StaffProfileServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        UpdateStaffProfileDto request = exchange.getIn().getBody(UpdateStaffProfileDto.class);
        String id = exchange.getIn().getHeader("id", String.class);

        ResponseEntity<?> response = serviceImp.update(id, request);
        exchange.getIn().setBody(response);
    }
}