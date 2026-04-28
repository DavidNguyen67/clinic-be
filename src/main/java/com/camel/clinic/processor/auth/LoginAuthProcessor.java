package com.camel.clinic.processor.auth;

import com.camel.clinic.dto.auth.LoginRequestDTO;
import com.camel.clinic.service.auth.AuthServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("loginAuthProcessor")
@AllArgsConstructor
@Slf4j
public class LoginAuthProcessor implements Processor {
    private final AuthServiceImp authServiceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        LoginRequestDTO request = exchange.getIn().getBody(LoginRequestDTO.class);
        ResponseEntity<?> response = authServiceImp.login(request);
        exchange.getIn().setBody(response);

    }
}
