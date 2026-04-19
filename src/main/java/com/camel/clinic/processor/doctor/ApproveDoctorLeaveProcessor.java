package com.camel.clinic.processor.doctor;

import com.camel.clinic.service.doctor.DoctorServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("approveDoctorLeaveProcessor")
@AllArgsConstructor
@Slf4j
public class ApproveDoctorLeaveProcessor implements Processor {
    private final DoctorServiceImp doctorServiceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        String leaveId = exchange.getIn().getHeader("id", String.class);
        Map<String, Object> requestBody = exchange.getIn().getBody(Map.class);
        ResponseEntity<?> response = doctorServiceImp.approveDoctorLeave(leaveId, requestBody);
        exchange.getMessage().setBody(response);
    }
}

