package com.camel.clinic.processor.doctorScheduleException;

import com.camel.clinic.dto.doctorScheduleException.UpdateDoctorScheduleExceptionDto;
import com.camel.clinic.service.doctorScheduleException.DoctorScheduleExceptionServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("doctorScheduleExceptionUpdateProcessor")
@AllArgsConstructor
@Slf4j
public class DoctorScheduleExceptionUpdateProcessor implements Processor {
    private final DoctorScheduleExceptionServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        UpdateDoctorScheduleExceptionDto request = exchange.getIn().getBody(UpdateDoctorScheduleExceptionDto.class);
        String id = exchange.getIn().getHeader("id", String.class);

        ResponseEntity<?> response = serviceImp.update(id, request);
        exchange.getIn().setBody(response);
    }
}