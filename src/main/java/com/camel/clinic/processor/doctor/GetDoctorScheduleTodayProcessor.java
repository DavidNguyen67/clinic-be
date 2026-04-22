package com.camel.clinic.processor.doctor;

import com.camel.clinic.service.doctor.DoctorServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("getDoctorScheduleTodayProcessor")
@AllArgsConstructor
@Slf4j
public class GetDoctorScheduleTodayProcessor implements Processor {
    private final DoctorServiceImp doctorServiceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        ResponseEntity<?> response = doctorServiceImp.getDoctorInfoSchedules();
        exchange.getMessage().setBody(response);
    }
}

