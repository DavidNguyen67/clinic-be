package com.camel.clinic.service.doctor;

import com.camel.clinic.entity.Doctor;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class DoctorServiceImp implements DoctorService {
    private final DoctorServiceInv doctorServiceInv;

    public ResponseEntity<?> createDoctor(Doctor body) throws JsonProcessingException {
        return doctorServiceInv.create(body);
    }

    public ResponseEntity<?> countAllDoctors() {
        return doctorServiceInv.count();
    }
}
