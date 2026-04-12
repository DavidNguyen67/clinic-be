package com.camel.clinic.entity;

import java.util.UUID;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "roles")
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Column(unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleName name;

    public enum RoleName {
        ROLE_ADMIN,
        ROLE_DOCTOR,
        ROLE_PATIENT,
        ROLE_STAFF
    }
}
