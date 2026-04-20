package com.camel.clinic.processor.clinicservice.admin;

import com.camel.clinic.service.clinicService.ClinicServiceImp;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component("adminDeleteClinicServiceProcessor")
@RequiredArgsConstructor
public class AdminDeleteClinicServiceProcessor implements Processor {

    private final ClinicServiceImp clinicServiceImp;

    @Override
    public void process(Exchange exchange) {
        String id = exchange.getIn().getHeader("id", String.class);
        exchange.getMessage().setBody(clinicServiceImp.delete(id));
    }
}

