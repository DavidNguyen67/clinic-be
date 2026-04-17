package com.camel.clinic.processor.auth;

import com.camel.clinic.service.auth.AuthServiceImp;
import com.camel.clinic.util.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("getUserProfileProcessor")
@AllArgsConstructor
@Slf4j
public class GetUserProfileProcessor implements Processor {
    private final AuthServiceImp authServiceImp;
    private final JwtUtil jwtUtil;

    @Override
    public void process(Exchange exchange) throws Exception {
        String authHeader = exchange.getIn().getHeader("Authorization", String.class);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getMessage().setBody(ResponseEntity.status(401).body("Missing token"));
            return;
        }

        String token = authHeader.substring(7);
        String email = jwtUtil.getEmailFromToken(token);

        ResponseEntity<?> response = authServiceImp.getUserProfile(email);
        exchange.getMessage().setBody(response);
    }
}

