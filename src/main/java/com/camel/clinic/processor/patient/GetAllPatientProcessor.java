package com.camel.clinic.processor.patient;

import com.camel.clinic.service.patient.PatientServiceImp;
import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;


@Component("getAllPatientProcessor")
@AllArgsConstructor
public class GetAllPatientProcessor implements Processor {
    private final PatientServiceImp patientServiceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        Object body = exchange.getIn().getBody(String.class);

        exchange.getMessage().setBody(body);
    }
}
