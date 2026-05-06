package com.camel.clinic.util;

import com.camel.clinic.entity.Appointment;
import com.camel.clinic.entity.Role;
import com.camel.clinic.exception.BadRequestException;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import static com.camel.clinic.entity.Appointment.AppointmentStatus.*;
import static com.camel.clinic.entity.Role.RoleName.*;

public final class AppointmentStatusTransition {

    private static final Map<Appointment.AppointmentStatus, Set<Appointment.AppointmentStatus>> VALID_TRANSITIONS =
            Map.of(
                    PENDING, EnumSet.of(CONFIRMED, CANCELLED),
                    CONFIRMED, EnumSet.of(CHECKED_IN, CANCELLED, NO_SHOW),
                    CHECKED_IN, EnumSet.of(IN_PROGRESS, CANCELLED, NO_SHOW),
                    IN_PROGRESS, EnumSet.of(COMPLETED, CANCELLED),
                    COMPLETED, EnumSet.noneOf(Appointment.AppointmentStatus.class),
                    CANCELLED, EnumSet.of(PENDING),
                    NO_SHOW, EnumSet.noneOf(Appointment.AppointmentStatus.class)
            );
    private static final Map<Role.RoleName, Set<Appointment.AppointmentStatus>> ALLOWED_TARGET_BY_ROLE =
            Map.of(
                    // PATIENT: chỉ tự hủy hoặc reactivate lịch của mình
                    PATIENT, EnumSet.of(CANCELLED, PENDING),

                    // DOCTOR: điều hành lịch khám + đánh dấu no-show
                    DOCTOR, EnumSet.of(CHECKED_IN, IN_PROGRESS, COMPLETED, NO_SHOW),

                    // RECEPTIONIST: confirm + check-in + hủy nếu cần
                    STAFF, EnumSet.of(CONFIRMED, CHECKED_IN, CANCELLED),

                    // ADMIN: full access mọi status
                    ADMIN, EnumSet.allOf(Appointment.AppointmentStatus.class)
            );
    // ── Special rules không thể diễn tả chỉ bằng map ────────────────────────
    private static final Set<Role.RoleName> REACTIVATION_ALLOWED = EnumSet.of(PATIENT, ADMIN);

    private AppointmentStatusTransition() {
    }


    public static void validate(Appointment.AppointmentStatus from,
                                Appointment.AppointmentStatus to,
                                Role.RoleName actor) {
        if (from == to) return;

        Set<Appointment.AppointmentStatus> reachable =
                VALID_TRANSITIONS.getOrDefault(from, EnumSet.noneOf(Appointment.AppointmentStatus.class));

        if (!reachable.contains(to)) {
            throw new BadRequestException(String.format(
                    "Invalid status transition: %s → %s", from, to));
        }

        if (from == CANCELLED && to == PENDING) {
            if (!REACTIVATION_ALLOWED.contains(actor)) {
                throw new BadRequestException(
                        "Only the patient who made the appointment and admin can reactivate a cancelled appointment");
            }
            return;
        }

        Set<Appointment.AppointmentStatus> permitted =
                ALLOWED_TARGET_BY_ROLE.getOrDefault(actor, EnumSet.noneOf(Appointment.AppointmentStatus.class));

        if (!permitted.contains(to)) {
            throw new BadRequestException(String.format(
                    "Role %s is not allowed to set appointment status to %s", actor, to));
        }
    }
}