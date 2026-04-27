package com.camel.clinic.service.schedule;

import com.camel.clinic.dto.schedule.ScheduleResponse;
import com.camel.clinic.entity.DoctorSchedule;
import com.camel.clinic.exception.BadRequestException;
import com.camel.clinic.repository.DoctorScheduleRepository;
import com.camel.clinic.service.BaseService;
import com.camel.clinic.service.CommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class ScheduleServiceInv extends BaseService<DoctorSchedule, DoctorScheduleRepository> {

    private final CommonService commonService;

    public ScheduleServiceInv(DoctorScheduleRepository repository, CommonService commonService) {
        super(DoctorSchedule::new, repository);
        this.commonService = commonService;
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> filterSchedules(Map<String, Object> queryParams) {
        try {
            int page = parseIntParam(queryParams, "page", 0);
            int size = parseIntParam(queryParams, "size", 20);
            String doctorIdStr = commonService.getStringParam(queryParams, "doctorId");
            UUID doctorId = null;
            if (doctorIdStr != null) {
                try {
                    doctorId = UUID.fromString(doctorIdStr);
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid doctorId format: {}", doctorIdStr, e);
                    throw new BadRequestException("doctorId must be a valid UUID");
                }
            }

            Pageable pageable = PageRequest.of(page, size);
            Page<ScheduleResponse> resultPage = repository.filterSchedules(doctorId, pageable).map(this::toScheduleResponse);
            return ResponseEntity.ok(commonService.buildPageResponse(resultPage));
        } catch (Exception e) {
            log.error("Error filtering doctors: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to filter doctors", e);
        }
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
