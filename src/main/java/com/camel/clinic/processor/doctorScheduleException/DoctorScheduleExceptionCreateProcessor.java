package com.camel.clinic.processor.doctorScheduleException;

import com.camel.clinic.dto.doctorScheduleException.CreateDoctorScheduleExceptionDto;
import com.camel.clinic.service.doctorScheduleException.DoctorScheduleExceptionServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("doctorScheduleExceptionCreateProcessor")
@AllArgsConstructor
@Slf4j
public class DoctorScheduleExceptionCreateProcessor implements Processor {
    private final DoctorScheduleExceptionServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        CreateDoctorScheduleExceptionDto request = exchange.getIn().getBody(CreateDoctorScheduleExceptionDto.class);
        ResponseEntity<?> response = serviceImp.create(request);
        exchange.getIn().setBody(response);
    }
}