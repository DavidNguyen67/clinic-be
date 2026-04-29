package com.camel.clinic.service.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImp implements UserService {
    private final UserServiceInv userServiceInv;

    @Override
    public ResponseEntity<?> list(Map<String, Object> queryParams) {
        return userServiceInv.list(queryParams);
    }

    @Override
    public ResponseEntity<?> count() {
        return userServiceInv.count();
    }

    @Override
    public ResponseEntity<?> retrieve(String id) {
        return userServiceInv.retrieve(id, null);
    }

}
