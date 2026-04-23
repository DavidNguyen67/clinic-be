package com.camel.clinic.service.schedule;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Slf4j
@Transactional
@AllArgsConstructor
public class ScheduleServiceImp implements ScheduleService {
    private final ScheduleServiceInv scheduleServiceInv;

    public ResponseEntity<?> filterSchedules(Map<String, Object> queryParams) {
        return scheduleServiceInv.filterSchedules(queryParams);
    }
}
