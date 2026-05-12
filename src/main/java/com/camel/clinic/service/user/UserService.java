package com.camel.clinic.service.user;

import com.camel.clinic.dto.user.CreateUserDto;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface UserService {
    ResponseEntity<?> list(Map<String, Object> queryParams);

    ResponseEntity<?> bulkCreate(List<CreateUserDto> requestBody);

    ResponseEntity<?> count();

    ResponseEntity<?> calculateStatistics();

    ResponseEntity<?> retrieve(String id);
}
