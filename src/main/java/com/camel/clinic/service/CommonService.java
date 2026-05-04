package com.camel.clinic.service;

import com.camel.clinic.entity.User;
import com.camel.clinic.exception.BadRequestException;
import com.camel.clinic.exception.UnauthorizedException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class CommonService {
    public static void requireRole(User user, String role) {
        if (!role.equals(user.getRole().name())) {
            throw new BadRequestException("Only user target has role: " + role);
        }
    }

    public static int parseIntParam(Map<String, Object> params, String key, int defaultValue) {
        Object val = params.get(key);
        if (val == null) return defaultValue;
        try {
            return Integer.parseInt(val.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static Boolean parseBoolean(Object value) {
        return switch (value) {
            case Boolean b -> b;
            case String s -> switch (s.trim().toLowerCase()) {
                case "true", "1", "yes" -> true;
                case "false", "0", "no" -> false;
                default -> null;
            };
            case null, default -> null;
        };
    }

    public static Date parseToDate(String rawDate) {
        if (rawDate == null || rawDate.isBlank()) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setLenient(false);
            return sdf.parse(rawDate);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public static Date parseToDate(String rawDate, String pattern) {
        if (rawDate == null || rawDate.isBlank()) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            sdf.setLenient(false);
            return sdf.parse(rawDate);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public static LocalDate parseToLocalDate(String rawDate) {
        if (rawDate == null || rawDate.isBlank()) {
            throw new IllegalArgumentException("date is required");
        }

        List<DateTimeFormatter> formatters = List.of(
                DateTimeFormatter.ISO_LOCAL_DATE,
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("d-M-yyyy")
        );

        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDate.parse(rawDate, formatter);
            } catch (DateTimeParseException ignored) {
                // Try next formatter.
            }
        }
        throw new IllegalArgumentException("Unsupported date format");
    }

    public static String getAuthHeader(Exchange exchange) {
        String authHeader = exchange.getIn().getHeader("Authorization", String.class);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Missing or invalid Authorization header");
        }
        return authHeader.substring(7);
    }

    public static String generateDoctorCode() {
        return generateCode("DOC");
    }

    public static String generateStaffCode() {
        return generateCode("STF");
    }

    public static String generatePatientCode() {
        return generateCode("PAT");
    }

    public static String generateAppointmentCode() {
        return generateCode("APT");
    }

    public static String generateCode(String prefix) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String shortCode = uuid.substring(0, 10).toUpperCase();

        return prefix + "-" + shortCode;
    }

    public static <E extends Enum<E>> E parseEnum(Class<E> enumClass, Object value) {
        if (value == null) return null;
        String s = value.toString().trim();
        if (s.isBlank()) return null;
        try {
            return Enum.valueOf(enumClass, s.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Failed to parse enum {} from value '{}'", enumClass.getSimpleName(), s);
            return null;
        }
    }

    public static UUID parseUuid(Object value) {
        if (value == null) return null;
        String s = value.toString().trim();
        if (s.isBlank()) return null;
        try {
            return UUID.fromString(s);
        } catch (IllegalArgumentException e) {
            log.warn("Failed to parse UUID from value '{}'", s);
            return null;
        }
    }

    public static String formatDate(Date date) {
        return formatDate(date, "dd/MM/yyyy");
    }

    public static String formatDate(Date date, String pattern) {
        if (date == null) return null;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        return sdf.format(date);
    }
}
