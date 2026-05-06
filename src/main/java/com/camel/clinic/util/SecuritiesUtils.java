package com.camel.clinic.util;

import com.camel.clinic.entity.PatientProfile;
import com.camel.clinic.entity.Role;
import com.camel.clinic.entity.User;
import com.camel.clinic.exception.BadRequestException;
import com.camel.clinic.exception.UnauthorizedException;
import com.camel.clinic.repository.PatientProfileRepository;
import com.camel.clinic.service.CommonService;
import org.apache.camel.Exchange;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Map;
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

    public static Role.RoleName getRole() {
        List<Role.RoleName> roles = getAuthorities();

        if (roles.contains(Role.RoleName.ADMIN)) return Role.RoleName.ADMIN;
        if (roles.contains(Role.RoleName.STAFF)) return Role.RoleName.STAFF;
        if (roles.contains(Role.RoleName.DOCTOR)) return Role.RoleName.DOCTOR;
        if (roles.contains(Role.RoleName.PATIENT)) return Role.RoleName.PATIENT;

        throw new BadRequestException("Cannot determine user role");
    }

    public static void injectPatientProfileId(
            Map<String, Object> queryParams,
            Exchange exchange,
            JwtUtil jwtUtil,
            PatientProfileRepository repo) {

        Role.RoleName role = getRole();
        if (role != Role.RoleName.PATIENT) return;

        String userId = jwtUtil.getUserIdFromToken(getAccessToken(exchange));
        PatientProfile profile = repo.findByUserId(CommonService.parseUuid(userId))
                .orElseThrow(() -> new RuntimeException(
                        "Patient profile not found for user ID: " + userId));

        queryParams.put("patientProfileId", profile.getId().toString());
    }
}
