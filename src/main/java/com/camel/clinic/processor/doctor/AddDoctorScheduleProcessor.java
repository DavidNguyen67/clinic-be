package com.camel.clinic.processor.doctor;

import com.camel.clinic.dto.doctor.DoctorScheduleRequestDTO;
import com.camel.clinic.service.doctor.DoctorServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("addDoctorScheduleProcessor")
@AllArgsConstructor
@Slf4j
public class AddDoctorScheduleProcessor implements Processor {
    private final DoctorServiceImp doctorServiceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        DoctorScheduleRequestDTO request = exchange.getIn().getBody(DoctorScheduleRequestDTO.class);
        ResponseEntity<?> response = doctorServiceImp.addDoctorSchedule(request);
        exchange.getMessage().setBody(response);
    }
}

