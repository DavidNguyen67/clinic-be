package com.camel.clinic.service.slotGenerator;

import com.camel.clinic.dto.appointment.TimeSlotDto;
import com.camel.clinic.entity.DoctorLeave;
import com.camel.clinic.entity.DoctorSchedule;
import com.camel.clinic.repository.AppointmentRepository;
import com.camel.clinic.repository.DoctorLeaveRepository;
import com.camel.clinic.repository.DoctorScheduleRepository;
import com.camel.clinic.service.CommonService;
import com.camel.clinic.util.DateTimeUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class SlotGeneratorServiceImp implements SlotGeneratorService {

    private final DoctorScheduleRepository scheduleRepo;
    private final AppointmentRepository appointmentRepo;
    private final DoctorLeaveRepository leaveRepo;
    private final CommonService commonService;

    public ResponseEntity<?> getAvailableSlots(Map<String, Object> queryParams) {
        // 1. Parse params
        UUID doctorId;
        Date dateParam;
        LocalDate localDate;
        try {
            doctorId = UUID.fromString((String) queryParams.get("doctorId"));
            localDate = commonService.parseDate((String) queryParams.get("date"));
            dateParam = Date.from(
                    localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
            );
        } catch (Exception e) {
            log.error("Invalid query params: {}", queryParams, e);
            throw new IllegalArgumentException("Invalid doctorId or date format");
        }

        List<DoctorLeave> approvedLeaves = leaveRepo.findApprovedByDoctorIdAndLeaveDate(doctorId, dateParam);
        if (approvedLeaves.stream().anyMatch(this::isFullDayLeave)) {
            log.info("Doctor {} is on full-day leave on {}", doctorId, localDate);
            return ResponseEntity.ok(List.of());
        }

        // 2. Lấy schedule theo thứ trong tuần (1=Mon … 7=Sun, khớp DayOfWeek.getValue())
//        int dayOfWeekValue = dateParam.getDayOfWeek().getValue();

        int dayOfWeekValue = localDate.getDayOfWeek().getValue() % 7;

        List<DoctorSchedule> schedules =
                scheduleRepo.findActiveByDoctorIdAndDayOfWeek(doctorId, dayOfWeekValue);

        if (schedules.isEmpty()) return ResponseEntity.ok(List.of());

        // 3. Build Set<LocalTime> các slot đã đặt — convert sang VN time
        Set<LocalTime> bookedTimes = buildBookedTimeSet(doctorId, dateParam);

        // 4. Sinh slot
        List<TimeSlotDto> result = new ArrayList<>();
        for (DoctorSchedule schedule : schedules) {
            result.addAll(generateSlots(schedule, bookedTimes, dateParam, approvedLeaves));
        }
        return ResponseEntity.ok(result);
    }

    // ------------------------------------------------------------------ //

    public List<TimeSlotDto> generateSlots(DoctorSchedule schedule,
                                           Set<LocalTime> bookedTimes,
                                           Date date,
                                           List<DoctorLeave> approvedLeaves) {

        // Convert java.util.Date → LocalTime (VN timezone)
        LocalTime scheduleStart = DateTimeUtils.toVnLocalTime(schedule.getStartTime());
        LocalTime scheduleEnd = DateTimeUtils.toVnLocalTime(schedule.getEndTime());
        int duration = schedule.getSlotDuration(); // slot_duration INTEGER

        // Chỉ bỏ slot đã qua nếu là hôm nay (theo giờ VN)
        boolean isToday = DateTimeUtils.toVnLocalDate(date).equals(DateTimeUtils.todayVn());
        LocalTime nowVn = DateTimeUtils.nowVn();

        List<TimeSlotDto> slots = new ArrayList<>();
        LocalTime cursor = scheduleStart;

        while (true) {
            LocalTime slotEnd = cursor.plusMinutes(duration);

            // Vượt quá khung giờ → dừng
            if (slotEnd.compareTo(scheduleEnd) > 0) break;

            // Slot đã qua (chỉ áp dụng hôm nay)
            boolean isPast = isToday && cursor.isBefore(nowVn);

            if (!isPast) {
                boolean booked = bookedTimes.contains(cursor);
                boolean onLeave = isOverlappingAnyLeave(cursor, slotEnd, approvedLeaves);
                boolean available = !booked && !onLeave;
                slots.add(TimeSlotDto.builder()
                        .id(schedule.getId())
                        .startTime(cursor)
                        .endTime(slotEnd)
                        .available(available)
                        .build());
            }

            cursor = slotEnd;
        }

        return slots;
    }

    /**
     * Truy vấn DB theo khoảng [00:00, 24:00) ngày đó (VN time → UTC để query).
     * Trả về Set<LocalTime> để lookup O(1).
     */
    public Set<LocalTime> buildBookedTimeSet(UUID doctorId, Date date) {
        // Đầu ngày và cuối ngày theo giờ VN, chuyển sang Date (UTC internally)

        Date startOfDay = DateTimeUtils.toDate(DateTimeUtils.toVnLocalDate(date), LocalTime.MIDNIGHT);
        Date endOfDay = DateTimeUtils.toDate(DateTimeUtils.toVnLocalDate(date).plusDays(1), LocalTime.MIDNIGHT);

        return appointmentRepo
                .findBookedStartTimesOnDate(doctorId, startOfDay, endOfDay)
                .stream()
                .map(DateTimeUtils::toVnLocalTime) // convert về VN time để so sánh
                .collect(Collectors.toCollection(HashSet::new));
    }

    private boolean isFullDayLeave(DoctorLeave leave) {
        return leave.getStartTime() == null || leave.getEndTime() == null;
    }

    private boolean isOverlappingAnyLeave(LocalTime slotStart, LocalTime slotEnd, List<DoctorLeave> approvedLeaves) {
        return approvedLeaves.stream().anyMatch(leave -> isOverlappingLeave(slotStart, slotEnd, leave));
    }

    private boolean isOverlappingLeave(LocalTime slotStart, LocalTime slotEnd, DoctorLeave leave) {
        if (isFullDayLeave(leave)) {
            return true;
        }

        LocalTime leaveStart = DateTimeUtils.toVnLocalTime(leave.getStartTime());
        LocalTime leaveEnd = DateTimeUtils.toVnLocalTime(leave.getEndTime());
        return slotStart.isBefore(leaveEnd) && slotEnd.isAfter(leaveStart);
    }
}
