package com.camel.clinic.processor.clinicservice.admin;

import com.camel.clinic.dto.clinicservice.ClinicServiceUpsertRequestDTO;
import com.camel.clinic.service.clinicService.ClinicServiceImp;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component("adminUpdateClinicServiceProcessor")
@RequiredArgsConstructor
public class AdminUpdateClinicServiceProcessor implements Processor {

    private final ClinicServiceImp clinicServiceImp;

    @Override
    public void process(Exchange exchange) {
        String id = exchange.getIn().getHeader("id", String.class);
        ClinicServiceUpsertRequestDTO request = exchange.getIn().getBody(ClinicServiceUpsertRequestDTO.class);
        exchange.getMessage().setBody(clinicServiceImp.update(id, request));
    }
}

