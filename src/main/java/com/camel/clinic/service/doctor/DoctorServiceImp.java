package com.camel.clinic.service.doctor;

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

    public ResponseEntity<?> getAllDoctors(Map<String, Object> queryParams) {
        return doctorServiceInv.list(queryParams);
    }

    public ResponseEntity<?> countAllDoctors() {
        return doctorServiceInv.count();
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> getTopDoctors() {
        return doctorServiceInv.getTopDoctors();
    }
}
