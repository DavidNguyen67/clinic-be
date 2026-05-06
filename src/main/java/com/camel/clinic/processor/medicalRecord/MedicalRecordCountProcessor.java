package com.camel.clinic.processor.medicalRecord;

import com.camel.clinic.service.medicalRecord.MedicalRecordServiceImp;
import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("medicalRecordCountProcessor")
@AllArgsConstructor
public class MedicalRecordCountProcessor implements Processor {
    private final MedicalRecordServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        ResponseEntity<?> response = serviceImp.count();
        exchange.getMessage().setBody(response);
    }
}
