package com.camel.clinic.processor.auth;

import com.camel.clinic.service.auth.AuthServiceImp;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component("logoutAuthProcessor")
@AllArgsConstructor
@Slf4j
public class LogoutAuthProcessor implements Processor {
    private final AuthServiceImp authServiceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        HttpServletRequest httpRequest = exchange.getIn()
                .getHeader(Exchange.HTTP_SERVLET_REQUEST, HttpServletRequest.class);

        String refreshToken = extractRefreshTokenFromCookie(httpRequest);

        String authHeader = exchange.getIn().getHeader("Authorization", String.class);
        String accessToken = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            accessToken = authHeader.substring(7);
        }

        ResponseEntity<?> response = authServiceImp.logout(refreshToken, accessToken);
        exchange.getIn().setBody(response);
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request == null || request.getCookies() == null) return null;

        return Arrays.stream(request.getCookies())
                .filter(c -> "refresh_token".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}

