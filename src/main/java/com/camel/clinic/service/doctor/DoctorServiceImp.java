package com.camel.clinic.service.doctor;

import com.camel.clinic.dto.doctor.DoctorLeaveRequestDTO;
import com.camel.clinic.dto.doctor.DoctorScheduleRequestDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Slf4j
@Transactional
@AllArgsConstructor
public class DoctorServiceImp implements DoctorService {
    private final DoctorServiceInv doctorServiceInv;

    public ResponseEntity<?> filterDoctors(Map<String, Object> queryParams) {
        return doctorServiceInv.filterDoctors(queryParams);
    }


    public ResponseEntity<?> countAllDoctors() {
        return doctorServiceInv.count();
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> getTopDoctors() {
        return doctorServiceInv.getTopDoctors();
    }

    @Override
    public ResponseEntity<?> getDoctorSchedules() {
        return doctorServiceInv.getDoctorSchedules();
    }

    @Override
    public ResponseEntity<?> addDoctorSchedule(DoctorScheduleRequestDTO requestBody) {
        return doctorServiceInv.addDoctorSchedule(requestBody);
    }

    @Override
    public ResponseEntity<?> deleteDoctorSchedule(String scheduleId) {
        return doctorServiceInv.deleteDoctorSchedule(scheduleId);
    }

    @Override
    public ResponseEntity<?> requestDoctorLeave(DoctorLeaveRequestDTO requestBody) {
        return doctorServiceInv.requestDoctorLeave(requestBody);
    }

    @Override
    public ResponseEntity<?> getDoctorLeaves(String doctorId) {
        return doctorServiceInv.getDoctorLeaves(doctorId);
    }

    @Override
    public ResponseEntity<?> approveDoctorLeave(String leaveId, Map<String, Object> requestBody) {
        return doctorServiceInv.approveDoctorLeave(leaveId, requestBody);
    }
}
