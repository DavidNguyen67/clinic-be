package com.camel.clinic.processor.auth;

import com.camel.clinic.dto.auth.VerifyOtpRequestDTO;
import com.camel.clinic.service.auth.AuthServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("verifyOtpAuthProcessor")
@AllArgsConstructor
@Slf4j
public class VerifyOtpAuthProcessor implements Processor {
    private final AuthServiceImp authServiceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        VerifyOtpRequestDTO request = exchange.getIn().getBody(VerifyOtpRequestDTO.class);
        ResponseEntity<?> response = authServiceImp.verifyOtp(request);
        exchange.getIn().setBody(response);
    }
}

