package com.camel.clinic.processor.doctorScheduleException;

import com.camel.clinic.service.doctorScheduleException.DoctorScheduleExceptionServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("doctorScheduleExceptionDeleteProcessor")
@AllArgsConstructor
@Slf4j
public class DoctorScheduleExceptionDeleteProcessor implements Processor {
    private final DoctorScheduleExceptionServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        String id = exchange.getIn().getHeader("id", String.class);

        ResponseEntity<?> response = serviceImp.delete(id);
        exchange.getIn().setBody(response);
    }
}