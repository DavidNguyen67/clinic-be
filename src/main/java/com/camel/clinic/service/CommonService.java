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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class CommonService {
    public void requireRole(User user, String role) {
        if (!role.equals(user.getRole().name())) {
            throw new BadRequestException("Only user target has role: " + role);
        }
    }

    public int parseIntParam(Map<String, Object> params, String key, int defaultValue) {
        Object val = params.get(key);
        if (val == null) return defaultValue;
        try {
            return Integer.parseInt(val.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public Date parseToDate(String rawDate) {
        if (rawDate == null || rawDate.isBlank()) {
            throw new IllegalArgumentException("date is required");
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setLenient(false);
            return sdf.parse(rawDate);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format, expected dd/MM/yyyy");
        }
    }

    public LocalDate parseToLocalDate(String rawDate) {
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

    public String getAuthHeader(Exchange exchange) {
        String authHeader = exchange.getIn().getHeader("Authorization", String.class);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Missing or invalid Authorization header");
        }
        return authHeader.substring(7);
    }

    public String generateDoctorCode() {
        return generateCode("DOC");
    }

    public String generateStaffCode() {
        return generateCode("STF");
    }

    public String generatePatientCode() {
        return generateCode("PAT");
    }

    public String generateCode(String prefix) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String shortCode = uuid.substring(0, 10).toUpperCase();

        return prefix + shortCode;
    }

    public <E extends Enum<E>> E parseEnum(Class<E> enumClass, Object value) {
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
}
