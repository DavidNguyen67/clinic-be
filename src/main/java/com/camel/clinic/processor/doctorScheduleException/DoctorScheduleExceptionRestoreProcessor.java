package com.camel.clinic.processor.doctorScheduleException;

import com.camel.clinic.service.doctorScheduleException.DoctorScheduleExceptionServiceImp;
import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("doctorScheduleExceptionRestoreProcessor")
@AllArgsConstructor
public class DoctorScheduleExceptionRestoreProcessor implements Processor {
    private final DoctorScheduleExceptionServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        String id = exchange.getIn().getHeader("id", String.class);
        ResponseEntity<?> response = serviceImp.restore(id);
        exchange.getMessage().setBody(response);
    }
}
