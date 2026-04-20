package com.camel.clinic.processor.clinicservice.admin;

import com.camel.clinic.dto.clinicservice.ClinicServiceUpsertRequestDTO;
import com.camel.clinic.service.clinicService.ClinicServiceImp;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component("adminCreateClinicServiceProcessor")
@RequiredArgsConstructor
public class AdminCreateClinicServiceProcessor implements Processor {

    private final ClinicServiceImp clinicServiceImp;

    @Override
    public void process(Exchange exchange) {
        ClinicServiceUpsertRequestDTO request = exchange.getIn().getBody(ClinicServiceUpsertRequestDTO.class);
        exchange.getMessage().setBody(clinicServiceImp.create(request));
    }
}

