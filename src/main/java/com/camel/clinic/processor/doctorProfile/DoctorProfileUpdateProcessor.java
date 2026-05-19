package com.camel.clinic.processor.doctorProfile;

import com.camel.clinic.dto.doctorProfile.UpdateDoctorProfileDto;
import com.camel.clinic.repository.DoctorProfileRepository;
import com.camel.clinic.service.doctorProfile.DoctorProfileServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("doctorProfileUpdateProcessor")
@AllArgsConstructor
@Slf4j
public class DoctorProfileUpdateProcessor implements Processor {
    private final DoctorProfileServiceImp serviceImp;
    private final DoctorProfileRepository repository;

    @Override
    public void process(Exchange exchange) throws Exception {
        UpdateDoctorProfileDto request = exchange.getIn().getBody(UpdateDoctorProfileDto.class);
        String id = exchange.getIn().getHeader("id", String.class);
        ResponseEntity<?> response = serviceImp.update(id, request);
        exchange.getIn().setBody(response);
    }
}