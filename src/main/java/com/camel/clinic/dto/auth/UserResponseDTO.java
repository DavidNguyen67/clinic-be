package com.camel.clinic.dto.auth;

import com.camel.clinic.entity.Role;
import com.camel.clinic.entity.User;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UserResponseDTO(
        UUID id,
        String email,
        String phone,
        String fullName,
        Role.RoleName role,
        User.UserStatus status
) {
    public static UserResponseDTO from(User u) {
        return UserResponseDTO.builder()
                .id(u.getId())
                .email(u.getEmail())
                .phone(u.getPhone())
                .fullName(u.getFullName())
                .role(u.getRole())
                .status(u.getStatus())
                .build();
    }
}

