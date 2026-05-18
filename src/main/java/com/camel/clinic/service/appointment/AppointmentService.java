package com.camel.clinic.service.appointment;

import com.camel.clinic.dto.appointment.CreateAppointmentDto;
import com.camel.clinic.dto.appointment.UpdateAppointmentDto;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface AppointmentService {
    ResponseEntity<?> calculatePatientStatistics(Map<String, Object> queryParams);

    ResponseEntity<?> calculateDoctorStatistics(Map<String, Object> queryParams);

    ResponseEntity<?> list(Map<String, Object> queryParams);

    ResponseEntity<?> count();

    ResponseEntity<?> retrieve(String id);

    ResponseEntity<?> retrieveNext(Map<String, Object> queryParams);

    ResponseEntity<?> create(CreateAppointmentDto requestBody);

    ResponseEntity<?> update(String id, UpdateAppointmentDto requestBody);

    ResponseEntity<?> delete(String id);

    ResponseEntity<?> restore(String id);
}
