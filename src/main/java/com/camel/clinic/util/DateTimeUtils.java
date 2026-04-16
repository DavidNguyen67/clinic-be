package com.camel.clinic.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

@UtilityClass
public class DateTimeUtils {

    public static final ZoneId VN_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    /**
     * java.util.Date → LocalTime theo giờ Việt Nam
     */
    public static LocalTime toVnLocalTime(Date date) {
        return date.toInstant()
                .atZone(VN_ZONE)
                .toLocalTime();
    }

    /**
     * java.util.Date → LocalDate theo giờ Việt Nam
     */
    public static LocalDate toVnLocalDate(Date date) {
        return date.toInstant()
                .atZone(VN_ZONE)
                .toLocalDate();
    }

    /**
     * LocalTime VN → java.util.Date (gắn vào một ngày cụ thể)
     */
    public static Date toDate(LocalDate date, LocalTime time) {
        return Date.from(
                LocalDateTime.of(date, time)
                        .atZone(VN_ZONE)
                        .toInstant()
        );
    }

    /**
     * "Bây giờ" theo giờ Việt Nam
     */
    public static LocalTime nowVn() {
        return LocalTime.now(VN_ZONE);
    }

    public static LocalDate todayVn() {
        return LocalDate.now(VN_ZONE);
    }
}