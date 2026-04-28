package com.camel.clinic.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;


@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_email", columnList = "email"),
        @Index(name = "idx_phone", columnList = "phone")
})
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class User extends SoftDeletableEntity {
    @NotBlank()
    @Email()
    @Column(unique = true, nullable = false, length = 255)
    private String email;

    @NotNull()
    @Column(name = "date_of_birth", nullable = false)
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "dd/MM/yyyy",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date dateOfBirth;

    @NotBlank()
    @Column(name = "password_hash", nullable = false, length = 255)
    @JsonIgnore()
    private String passwordHash;

    @NotBlank()
    @Column(unique = true, nullable = false, length = 20)
    private String phone;

    @NotBlank()
    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role.RoleName role = Role.RoleName.PATIENT;

    @Column(length = 500)
    private String pathAvatar;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status = UserStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Gender gender = Gender.OTHER;

    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified = false;

    @Column(name = "phone_verified", nullable = false)
    private Boolean phoneVerified = false;

    @Column(name = "last_login")
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "dd/MM/yyyy",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date lastLogin;

    public enum UserStatus {
        ACTIVE, INACTIVE, BANNED
    }

    public enum Gender {
        MALE, FEMALE, OTHER
    }
}
