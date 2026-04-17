package com.camel.clinic.processor.doctor;

import com.camel.clinic.service.doctor.DoctorServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("deleteDoctorScheduleProcessor")
@AllArgsConstructor
@Slf4j
public class DeleteDoctorScheduleProcessor implements Processor {
    private final DoctorServiceImp doctorServiceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        String scheduleId = exchange.getIn().getHeader("id", String.class);
        ResponseEntity<?> response = doctorServiceImp.deleteDoctorSchedule(scheduleId);
        exchange.getMessage().setBody(response);
    }
}

