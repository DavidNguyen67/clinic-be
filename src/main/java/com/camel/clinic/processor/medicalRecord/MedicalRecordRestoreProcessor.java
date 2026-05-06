package com.camel.clinic.processor.medicalRecord;

import com.camel.clinic.service.medicalRecord.MedicalRecordServiceImp;
import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("medicalRecordRestoreProcessor")
@AllArgsConstructor
public class MedicalRecordRestoreProcessor implements Processor {
    private final MedicalRecordServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        String id = exchange.getIn().getHeader("id", String.class);
        ResponseEntity<?> response = serviceImp.restore(id);
        exchange.getMessage().setBody(response);
    }
}
