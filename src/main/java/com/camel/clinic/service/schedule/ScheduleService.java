package com.camel.clinic.service.schedule;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface ScheduleService {
    ResponseEntity<?> filterSchedules(Map<String, Object> queryParams);
}

