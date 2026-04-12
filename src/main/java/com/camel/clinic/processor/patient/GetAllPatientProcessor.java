package com.camel.clinic.processor.patient;

import com.camel.clinic.service.patient.PatientServiceImp;
import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component("getAllPatientProcessor")
@AllArgsConstructor
public class GetAllPatientProcessor implements Processor {
    private final PatientServiceImp patientServiceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        Map<String, Object> queryParams = exchange.getIn().getHeaders();
        String queryString = queryParams.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .reduce((a, b) -> a + "&" + b)
                .orElse("");

        ResponseEntity<?> response = patientServiceImp.getAllPatient(queryParams);

        exchange.getMessage().setBody(response);
    }
}
