package com.camel.clinic.service.doctor;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface DoctorService {
    ResponseEntity<?> countAllDoctors();

    ResponseEntity<?> getTopDoctors();

    ResponseEntity<?> filterDoctors(Map<String, Object> queryParams);

    // Doctor Schedule Management
    ResponseEntity<?> getDoctorSchedules();

    ResponseEntity<?> addDoctorSchedule(Map<String, Object> requestBody);

    ResponseEntity<?> deleteDoctorSchedule(String scheduleId);

    // Doctor Leave Management
    ResponseEntity<?> requestDoctorLeave(Map<String, Object> requestBody);

    ResponseEntity<?> getDoctorLeaves(String doctorId);

    ResponseEntity<?> approveDoctorLeave(String leaveId, Map<String, Object> requestBody);
}
