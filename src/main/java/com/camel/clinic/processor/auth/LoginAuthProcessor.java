package com.camel.clinic.processor.auth;

import com.camel.clinic.dto.auth.LoginRequestDTO;
import com.camel.clinic.service.auth.AuthServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("loginAuthProcessor")
@AllArgsConstructor
@Slf4j
public class LoginAuthProcessor implements Processor {
    private final AuthServiceImp authServiceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        LoginRequestDTO request = exchange.getIn().getBody(LoginRequestDTO.class);

        ResponseEntity<?> response = authServiceImp.login(request);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();

        if (responseBody != null && responseBody.get("refreshToken") != null) {
            exchange.getIn().setHeader("Set-Cookie",
                    "refreshToken=" + responseBody.get("refreshToken") + "; HttpOnly; Path=/; Max-Age=604800");
        }
        exchange.getIn().setBody(response);

    }
}
