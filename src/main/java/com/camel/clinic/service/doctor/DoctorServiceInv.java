package com.camel.clinic.service.doctor;

import com.camel.clinic.entity.Doctor;
import com.camel.clinic.repository.DoctorRepository;
import com.camel.clinic.service.BaseService;
import org.springframework.stereotype.Service;

@Service
public class DoctorServiceInv extends BaseService<Doctor, DoctorRepository> {
    public DoctorServiceInv(DoctorRepository repository) {
        super(Doctor::new, repository);
    }
}