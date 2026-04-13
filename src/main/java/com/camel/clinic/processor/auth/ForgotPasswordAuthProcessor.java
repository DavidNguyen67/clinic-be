package com.camel.clinic.processor.auth;

import com.camel.clinic.dto.auth.ForgotPasswordRequestDTO;
import com.camel.clinic.service.auth.AuthServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("forgotPasswordAuthProcessor")
@AllArgsConstructor
@Slf4j
public class ForgotPasswordAuthProcessor implements Processor {
    private final AuthServiceImp authServiceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        ForgotPasswordRequestDTO request = exchange.getIn().getBody(ForgotPasswordRequestDTO.class);
        ResponseEntity<?> response = authServiceImp.forgotPassword(request);
        exchange.getIn().setBody(response);
    }
}

