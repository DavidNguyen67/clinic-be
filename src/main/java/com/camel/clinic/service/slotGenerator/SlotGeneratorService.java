package com.camel.clinic.service.slotGenerator;

import com.camel.clinic.dto.TimeSlotDto;
import com.camel.clinic.entity.DoctorSchedule;
import org.springframework.http.ResponseEntity;

import java.time.LocalTime;
import java.util.*;

public interface SlotGeneratorService {
    ResponseEntity<?> getAvailableSlots(Map<String, Object> queryParams);

    List<TimeSlotDto> generateSlots(
            DoctorSchedule schedule,
            Set<LocalTime> bookedTimes,
            Date date);

    Set<LocalTime> buildBookedTimeSet(UUID doctorId, Date date);
}
