package com.camel.clinic.processor.medicalRecord;

import com.camel.clinic.dto.medicalRecord.UpdateMedicalRecordDto;
import com.camel.clinic.service.medicalRecord.MedicalRecordServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("medicalRecordUpdateProcessor")
@AllArgsConstructor
@Slf4j
public class MedicalRecordUpdateProcessor implements Processor {
    private final MedicalRecordServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        UpdateMedicalRecordDto request = exchange.getIn().getBody(UpdateMedicalRecordDto.class);
        String id = exchange.getIn().getHeader("id", String.class);

        ResponseEntity<?> response = serviceImp.update(id, request);
        exchange.getIn().setBody(response);
    }
}