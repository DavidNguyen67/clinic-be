package com.camel.clinic.service.schedule;

import com.camel.clinic.dto.schedule.ScheduleResponse;
import com.camel.clinic.entity.DoctorSchedule;
import com.camel.clinic.repository.DoctorScheduleRepository;
import com.camel.clinic.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class ScheduleServiceInv extends BaseService<DoctorSchedule, DoctorScheduleRepository> {


    public ScheduleServiceInv(DoctorScheduleRepository repository) {
        super(DoctorSchedule::new, repository);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> filterSchedules(Map<String, Object> queryParams) {
        try {
            int page = parseIntParam(queryParams, "page", 0);
            int size = parseIntParam(queryParams, "size", 20);
            String doctorIdStr = getStringParam(queryParams, "doctorId");
            UUID doctorId = null;
            if (doctorIdStr != null) {
                try {
                    doctorId = UUID.fromString(doctorIdStr);
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid doctorId format: {}", doctorIdStr, e);
                    return ResponseEntity.badRequest().body(Map.of("error", "Invalid doctorId format",
                            "message", "doctorId must be a valid UUID"));
                }
            }

            Pageable pageable = PageRequest.of(page, size);
            Page<ScheduleResponse> resultPage = repository.filterSchedules(doctorId, pageable).map(this::toScheduleResponse);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("data", resultPage.getContent());
            response.put("page", resultPage.getNumber());
            response.put("size", resultPage.getSize());
            response.put("totalItems", resultPage.getTotalElements());
            response.put("totalPages", resultPage.getTotalPages());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error filtering doctors: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to filter doctors", "message", e.getMessage()));
        }
    }


    private String getStringParam(Map<String, Object> queryParams, String... keys) {
        for (String key : keys) {
            Object value = queryParams.get(key);
            if (value instanceof String str && !str.isBlank()) {
                return str.trim();
            }
        }
        return null;
    }

    public ScheduleResponse toScheduleResponse(DoctorSchedule schedule) {
        return ScheduleResponse.builder()
                .id(schedule.getId())
                .dayOfWeek(schedule.getDayOfWeek())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .slotDuration(schedule.getSlotDuration())
                .maxPatientsPerSlot(schedule.getMaxPatientsPerSlot())
                .location(schedule.getLocation())
                .isActive(schedule.getIsActive())
                .build();
    }
}
