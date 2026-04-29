package com.camel.clinic.service.doctorScheduleException;

import com.camel.clinic.dto.doctorScheduleException.CreateDoctorScheduleExceptionDto;
import com.camel.clinic.dto.doctorScheduleException.UpdateDoctorScheduleExceptionDto;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface DoctorScheduleExceptionService {
    ResponseEntity<?> list(Map<String, Object> queryParams);

    ResponseEntity<?> count();

    ResponseEntity<?> retrieve(String id);

    ResponseEntity<?> create(CreateDoctorScheduleExceptionDto requestBody);

    ResponseEntity<?> update(String id, UpdateDoctorScheduleExceptionDto requestBody);

    ResponseEntity<?> delete(String id);

    ResponseEntity<?> restore(String id);
}
