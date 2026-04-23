package com.camel.clinic.service.doctor;

import com.camel.clinic.dto.doctor.DoctorLeaveApproveRequestDTO;
import com.camel.clinic.dto.doctor.DoctorLeaveRequestDTO;
import com.camel.clinic.dto.doctor.DoctorScheduleRequestDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface DoctorService {
    ResponseEntity<?> countAllDoctors();

    ResponseEntity<?> getTopDoctors();

    ResponseEntity<?> filterDoctors(Map<String, Object> queryParams);

    // Doctor Schedule Management
    ResponseEntity<?> getDoctorSchedules();

    ResponseEntity<?> getDoctorInfoSchedules();

    ResponseEntity<?> addDoctorSchedules(List<DoctorScheduleRequestDTO> requestBody);

    ResponseEntity<?> deleteDoctorSchedule(String scheduleId);

    // Doctor Leave Management
    ResponseEntity<?> requestDoctorLeave(DoctorLeaveRequestDTO requestBody);

    ResponseEntity<?> getDoctorLeaves(String doctorId);

    ResponseEntity<?> approveDoctorLeave(String leaveId, DoctorLeaveApproveRequestDTO requestBody);

    ResponseEntity<?> getDoctorInfo(String doctorId);
}

