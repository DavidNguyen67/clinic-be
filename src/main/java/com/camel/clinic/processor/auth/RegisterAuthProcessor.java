package com.camel.clinic.processor.auth;

import com.camel.clinic.dto.auth.RegisterRequestDTO;
import com.camel.clinic.service.auth.AuthServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("registerAuthProcessor")
@AllArgsConstructor
@Slf4j
public class RegisterAuthProcessor implements Processor {
    private final AuthServiceImp authServiceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        RegisterRequestDTO request = exchange.getIn().getBody(RegisterRequestDTO.class);
        ResponseEntity<?> response = authServiceImp.register(request);
        exchange.getIn().setBody(response);
    }
}

