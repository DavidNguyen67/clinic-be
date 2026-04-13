package com.camel.clinic.dto.auth;

public record LoginResponse(
        String tokenType,
        String accessToken,
        long expiresInSeconds
) {
}

