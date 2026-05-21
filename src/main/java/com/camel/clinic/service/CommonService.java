package com.camel.clinic.service;

import com.camel.clinic.dto.DateRange;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class CommonService {
    private static final ObjectMapper mapper = new ObjectMapper();

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

    public static Long parseToLong(Object value) {
        if (value == null) return null;
        try {
            return Long.parseLong(value.toString().trim());
        } catch (NumberFormatException e) {
            log.warn("Failed to parse Long from value '{}'", value);
            return null;
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

    public static String generatePromotionCode() {
        return generateCode("PRM");
    }

    public static String generateInvoiceCode() {
        return generateCode("INV");
    }

    public static String generateMedicalRecordCode() {
        return generateCode("REC");
    }

    public static String generateCode(String prefix) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String shortCode = uuid.substring(0, 10).toUpperCase();

        return prefix + "-" + shortCode;
    }

    public static <E extends Enum<E>> E parseToEnum(Class<E> enumClass, Object value) {
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

    public static UUID parseToUuid(Object value) {
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

    public static Date getCurrentDate() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static <T> T parsePayload(Object payload, Class<T> clazz) {
        try {
            if (payload instanceof String json) {
                return mapper.readValue(json, clazz);
            }

            return mapper.convertValue(payload, clazz);

        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Failed to parse payload: " + e.getMessage());
        }
    }

    public static DateRange buildLastMonthRange(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, -1);      // roll back one month
        return buildMonthRange(cal.getTime());
    }

    public static DateRange buildMonthRange(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date start = cal.getTime();

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);     // BUG FIX: was missing
        cal.set(Calendar.MILLISECOND, 999);
        Date end = cal.getTime();

        return new DateRange(start, end);
    }

    public static <T> List<T> parseToList(Object value, Function<String, T> parser) {
        if (value == null) return Collections.emptyList();
        String s = value.toString().trim();
        if (s.isBlank()) return Collections.emptyList();

        if (s.startsWith("[") && s.endsWith("]")) {
            s = s.substring(1, s.length() - 1).trim();
        }

        if (s.isBlank()) return Collections.emptyList();

        return Arrays.stream(s.split(","))
                .map(String::trim)
                .filter(part -> !part.isBlank())
                .map(part -> {
                    try {
                        return parser.apply(part);
                    } catch (Exception e) {
                        log.warn("Failed to parse value '{}'", part);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
