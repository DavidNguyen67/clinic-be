package com.camel.clinic.service.user;

import com.camel.clinic.dto.user.CreateUserDto;
import com.camel.clinic.dto.user.UpdateUserDto;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface UserService {
    ResponseEntity<?> list(Map<String, Object> queryParams);

    ResponseEntity<?> count();

    ResponseEntity<?> retrieve(String id);

    ResponseEntity<?> create(CreateUserDto requestBody);

    ResponseEntity<?> update(String id, UpdateUserDto requestBody);

    ResponseEntity<?> delete(String id);

    ResponseEntity<?> restore(String id);
}
