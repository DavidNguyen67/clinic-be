package com.camel.clinic.service.user;

import com.camel.clinic.dto.user.CreateUserDto;
import com.camel.clinic.dto.user.UpdateUserDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImp implements UserService {
    private final UserServiceInv specialtyServiceInv;

    @Override
    public ResponseEntity<?> count() {
        return specialtyServiceInv.count();
    }

    @Override
    public ResponseEntity<?> retrieve(String id) {
        return specialtyServiceInv.retrieve(id, null);
    }

    @Override
    public ResponseEntity<?> create(CreateUserDto requestBody) {
        return null;
    }

    @Override
    public ResponseEntity<?> update(String id, UpdateUserDto requestBody) {
        return null;
    }


    @Override
    public ResponseEntity<?> delete(String id) {
        return specialtyServiceInv.delete(id);
    }

    @Override
    public ResponseEntity<?> restore(String id) {
        return specialtyServiceInv.restore(id);
    }

    public ResponseEntity<?> list(Map<String, Object> queryParams) {
        return specialtyServiceInv.list(queryParams);
    }
}
