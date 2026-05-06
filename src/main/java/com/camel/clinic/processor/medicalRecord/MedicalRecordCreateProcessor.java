package com.camel.clinic.processor.medicalRecord;

import com.camel.clinic.dto.medicalRecord.CreateMedicalRecordDto;
import com.camel.clinic.service.medicalRecord.MedicalRecordServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("medicalRecordCreateProcessor")
@AllArgsConstructor
@Slf4j
public class MedicalRecordCreateProcessor implements Processor {
    private final MedicalRecordServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        CreateMedicalRecordDto request = exchange.getIn().getBody(CreateMedicalRecordDto.class);
        ResponseEntity<?> response = serviceImp.create(request);
        exchange.getIn().setBody(response);
    }
}