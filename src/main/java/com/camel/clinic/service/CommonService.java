package com.camel.clinic.service;

import com.camel.clinic.entity.User;
import com.camel.clinic.exception.NotFoundException;
import com.camel.clinic.exception.UnauthorizedException;
import com.camel.clinic.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class CommonService {
    private final UserRepository userRepository;

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    public void requireRole(User user, String role) {
        if (!role.equals(user.getRole().name())) {
            throw new UnauthorizedException("Required role: " + role);
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

    public String getStringParam(Map<String, Object> queryParams, String... keys) {
        for (String key : keys) {
            Object value = queryParams.get(key);
            if (value instanceof String str && !str.isBlank()) {
                return str.trim();
            }
        }
        return null;
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

    public <T> Map<String, Object> buildPageResponse(Page<T> page) {
        List<?> data = page.getContent();
        long totalItems = page.getTotalElements();
        int size = page.getSize();
        return Map.of(
                "data", data,
                "totalItems", totalItems,
                "page", page.getNumber(),
                "size", size,
                "totalPages", (int) Math.ceil((double) totalItems / size)
        );
    }

    public Date parseAppointmentDate(Object raw) {
        if (raw == null || raw.toString().isBlank()) return null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setLenient(false);
            return sdf.parse(raw.toString());
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid appointmentDate format, expected dd/MM/yyyy");
        }
    }

    public Pageable buildPageable(Map<String, Object> queryParams) {
        int page = parseIntParam(queryParams, "page", 0);
        int size = parseIntParam(queryParams, "size", 20);
        String sortBy = (String) queryParams.getOrDefault("sortBy", "id");
        String sortDir = (String) queryParams.getOrDefault("sortDir", "asc");
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        return PageRequest.of(page, size, sort);
    }

    public String formatToDdMMyyyy(LocalDate date) {
        if (date == null) {
            return null; // hoặc throw tùy bạn
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(formatter);
    }

    public String formatToDdMMyyyy(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(date);
    }
}
