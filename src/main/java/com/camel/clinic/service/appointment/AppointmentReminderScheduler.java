package com.camel.clinic.service.appointment;

import com.camel.clinic.entity.Appointment;
import com.camel.clinic.repository.AppointmentRepository;
import com.camel.clinic.util.DateTimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppointmentReminderScheduler {

    private final AppointmentRepository appointmentRepository;

    // Every day at 08:00 VN: find appointments happening in the next 24 hours.
    @Scheduled(cron = "0 0 8 * * *", zone = "Asia/Ho_Chi_Minh")
    public void sendDayBeforeReminders() {
        LocalDateTime from = LocalDateTime.now(DateTimeUtils.VN_ZONE).plusHours(23);
        LocalDateTime to = LocalDateTime.now(DateTimeUtils.VN_ZONE).plusHours(25);
        dispatchReminderWindow(from, to, "24h");
    }

    // Every hour: find appointments happening in the next hour.
    @Scheduled(cron = "0 0 * * * *", zone = "Asia/Ho_Chi_Minh")
    public void sendHourBeforeReminders() {
        LocalDateTime from = LocalDateTime.now(DateTimeUtils.VN_ZONE).plusMinutes(55);
        LocalDateTime to = LocalDateTime.now(DateTimeUtils.VN_ZONE).plusMinutes(65);
        dispatchReminderWindow(from, to, "1h");
    }

    private void dispatchReminderWindow(LocalDateTime from, LocalDateTime to, String windowLabel) {
        Date fromDate = Date.from(from.atZone(DateTimeUtils.VN_ZONE).toInstant());
        Date toDate = Date.from(to.atZone(DateTimeUtils.VN_ZONE).toInstant());

        List<Appointment> appointments = appointmentRepository.findConfirmedByStartTimeBetween(fromDate, toDate);
        for (Appointment appointment : appointments) {
            // TODO: Send reminder email/SMS and push notification for appointment reminders.
            log.info("[REMINDER-{}] appointmentCode={} patientId={}",
                    windowLabel,
                    appointment.getAppointmentCode(),
                    appointment.getPatient() != null ? appointment.getPatient().getId() : null);
        }
    }
}

