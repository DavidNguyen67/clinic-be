package com.camel.clinic.processor.clinicservice.admin;

import com.camel.clinic.service.clinicService.ClinicServiceImp;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("adminListClinicServicesProcessor")
@RequiredArgsConstructor
public class AdminListClinicServicesProcessor implements Processor {

    private final ClinicServiceImp clinicServiceImp;

    @Override
    public void process(Exchange exchange) {
        Map<String, Object> queryParams = exchange.getIn().getHeaders();
        exchange.getMessage().setBody(clinicServiceImp.list(queryParams));
    }
}

