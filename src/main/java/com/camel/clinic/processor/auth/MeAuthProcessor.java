package com.camel.clinic.processor.auth;

import com.camel.clinic.exception.UnauthorizedException;
import com.camel.clinic.service.auth.AuthServiceImp;
import com.camel.clinic.util.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("meAuthProcessor")
@AllArgsConstructor
@Slf4j
public class MeAuthProcessor implements Processor {
    private final AuthServiceImp authServiceImp;
    private final JwtUtil jwtUtil;

    @Override
    public void process(Exchange exchange) throws Exception {
        String authHeader = exchange.getIn().getHeader("Authorization", String.class);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Missing token");
        }
        String token = authHeader.substring(7);
        String email = jwtUtil.getEmailFromToken(token);
        ResponseEntity<?> response = authServiceImp.me(email);
        exchange.getIn().setBody(response);
    }
}

