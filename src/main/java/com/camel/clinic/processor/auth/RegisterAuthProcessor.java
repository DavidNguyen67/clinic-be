package com.camel.clinic.processor.auth;

import com.camel.clinic.dto.auth.RegisterRequestDTO;
import com.camel.clinic.service.auth.AuthServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("registerAuthProcessor")
@AllArgsConstructor
@Slf4j
public class RegisterAuthProcessor implements Processor {
    private final AuthServiceImp authServiceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        RegisterRequestDTO request = exchange.getIn().getBody(RegisterRequestDTO.class);
        ResponseEntity<?> response = authServiceImp.register(request);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();

        if (responseBody != null && responseBody.get("refreshToken") != null) {
            exchange.getIn().setHeader("Set-Cookie",
                    "refreshToken=" + responseBody.get("refreshToken") + "; HttpOnly; Path=/; Max-Age=604800");
        }
        exchange.getIn().setBody(response);
    }
}

