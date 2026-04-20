package com.camel.clinic.config;

import com.camel.clinic.dto.RestErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public RestAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        RestErrorResponse errorResponse = new RestErrorResponse();
        errorResponse.setStatusCode("FORBIDDEN");
        errorResponse.setStatusCodeValue(HttpServletResponse.SC_FORBIDDEN);

        String reason = detectReason(request, authException);
        errorResponse.setBody(reason);

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    private String detectReason(HttpServletRequest request, AuthenticationException ex) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || authHeader.isBlank()) {
            return "MISSING_TOKEN";
        }
        if (!authHeader.startsWith("Bearer ")) {
            return "INVALID_TOKEN_FORMAT";
        }

        String message = ex.getMessage() != null ? ex.getMessage().toLowerCase() : "";

        if (message.contains("expired")) {
            return "TOKEN_EXPIRED";
        }
        if (message.contains("signature")) {
            return "INVALID_SIGNATURE";
        }
        if (message.contains("malformed") || message.contains("illegal")) {
            return "MALFORMED_TOKEN";
        }
        if (message.contains("unsupported")) {
            return "UNSUPPORTED_TOKEN";
        }

        return "INVALID_TOKEN";
    }
}

