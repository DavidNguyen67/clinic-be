package com.camel.clinic.processor.auth;

import com.camel.clinic.dto.auth.ResetPasswordRequestDTO;
import com.camel.clinic.service.auth.AuthServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("resetPasswordAuthProcessor")
@AllArgsConstructor
@Slf4j
public class ResetPasswordAuthProcessor implements Processor {
    private final AuthServiceImp authServiceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        ResetPasswordRequestDTO request = exchange.getIn().getBody(ResetPasswordRequestDTO.class);
        ResponseEntity<?> response = authServiceImp.resetPassword(request);
        exchange.getIn().setBody(response);
    }
}

