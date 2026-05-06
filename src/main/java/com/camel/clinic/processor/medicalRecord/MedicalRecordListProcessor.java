package com.camel.clinic.processor.medicalRecord;

import com.camel.clinic.repository.PatientProfileRepository;
import com.camel.clinic.service.medicalRecord.MedicalRecordServiceImp;
import com.camel.clinic.util.JwtUtil;
import com.camel.clinic.util.SecuritiesUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("medicalRecordListProcessor")
@AllArgsConstructor
@Slf4j
public class MedicalRecordListProcessor implements Processor {
    private final MedicalRecordServiceImp serviceImp;
    private final JwtUtil jwtUtil;
    private final PatientProfileRepository patientProfileRepository;

    @Override
    public void process(Exchange exchange) {
        Map<String, Object> queryParams = exchange.getIn().getHeaders();

        SecuritiesUtils.injectPatientProfileId(queryParams, exchange, jwtUtil, patientProfileRepository);

        exchange.getMessage().setBody(serviceImp.list(queryParams));
    }
}