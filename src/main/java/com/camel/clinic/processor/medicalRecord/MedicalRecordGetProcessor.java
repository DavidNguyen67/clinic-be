package com.camel.clinic.processor.medicalRecord;

import com.camel.clinic.service.medicalRecord.MedicalRecordServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("medicalRecordGetProcessor")
@AllArgsConstructor
@Slf4j
public class MedicalRecordGetProcessor implements Processor {
    private final MedicalRecordServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) {
        String id = exchange.getIn().getHeader("id", String.class);
        ResponseEntity<?> response = serviceImp.retrieve(id);
        exchange.getIn().setBody(response);
    }
}