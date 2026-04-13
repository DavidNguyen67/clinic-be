package com.camel.clinic.processor.auth;

import com.camel.clinic.dto.auth.RefreshRequestDTO;
import com.camel.clinic.service.auth.AuthServiceImp;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;

@Component("refreshAuthProcessor")
@AllArgsConstructor
@Slf4j
public class RefreshAuthProcessor implements Processor {

    private final AuthServiceImp authServiceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        HttpServletRequest httpRequest = exchange.getIn()
                .getHeader(Exchange.HTTP_SERVLET_REQUEST, HttpServletRequest.class);

        String refreshToken = extractRefreshTokenFromCookie(httpRequest);

        if (refreshToken == null || refreshToken.isBlank()) {
            exchange.getIn().setBody(
                    ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(Map.of("message", "Refresh token cookie not found"))
            );
        }

        RefreshRequestDTO request = new RefreshRequestDTO();
        request.setRefreshToken(refreshToken);
        ResponseEntity<?> response = authServiceImp.refresh(request);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();

        if (responseBody != null && responseBody.get("refreshToken") != null) {
            exchange.getIn().setHeader("Set-Cookie",
                    "refreshToken=" + responseBody.get("refreshToken") + "; HttpOnly; Path=/; Max-Age=604800");
        }
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

