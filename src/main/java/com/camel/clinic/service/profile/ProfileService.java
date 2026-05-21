package com.camel.clinic.service.profile;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface ProfileService {
    ResponseEntity<?> list(Map<String, Object> queryParams);
}
