package com.camel.clinic.util;

import com.camel.clinic.entity.Role;
import com.camel.clinic.entity.User;
import com.camel.clinic.exception.BadRequestException;
import com.camel.clinic.exception.UnauthorizedException;
import org.apache.camel.Exchange;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Objects;

public class SecuritiesUtils {
    public static String getAccessToken(Exchange exchange) {
        String authHeader = exchange.getIn().getHeader("Authorization", String.class);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Missing or invalid Authorization header");
        }
        return authHeader.substring(7);
    }

    public static List<Role.RoleName> getAuthorities() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .map(a -> {
                    try {
                        return Role.RoleName.valueOf(a.getAuthority());
                    } catch (IllegalArgumentException e) {
                        return null; // bỏ qua authority không map được
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    public static void requireRole(User user, String role) {
        if (!role.equals(user.getRole().name())) {
            throw new BadRequestException("Only user target has role: " + role);
        }
    }
}
