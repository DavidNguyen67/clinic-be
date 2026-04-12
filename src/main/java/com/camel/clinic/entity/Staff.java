package com.camel.clinic.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "staff", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_staff_code", columnList = "staff_code"),
        @Index(name = "idx_department", columnList = "department"),
        @Index(name = "idx_status", columnList = "status")
})
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Staff extends SoftDeletableEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false, foreignKey = @ForeignKey(name = "fk_staff_user"))
    @NotNull()
    private User user;

    @NotBlank()
    @Column(name = "staff_code", unique = true, nullable = false, length = 20)
    private String staffCode;

    @Column(length = 100)
    private String position;

    @Column(length = 100)
    private String department;

    @Column(name = "hire_date")
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "dd/MM/yyyy",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date hireDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StaffStatus status = StaffStatus.active;

    public enum StaffStatus {
        active, on_leave, resigned
    }
}
