package com.camel.clinic.service.doctor;

import com.camel.clinic.dto.DoctorDTO;
import com.camel.clinic.entity.Doctor;
import com.camel.clinic.repository.DoctorRepository;
import com.camel.clinic.service.BaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorServiceInv extends BaseService<Doctor, DoctorRepository> {
    public DoctorServiceInv(DoctorRepository repository) {
        super(Doctor::new, repository);
    }

    public ResponseEntity<?> getTopDoctors() {
        List<DoctorDTO> doctors = repository.getTopDoctors();

        return ResponseEntity.ok(doctors);
    }
}