package com.camel.clinic.service.doctor;

import com.camel.clinic.entity.Doctor;
import com.camel.clinic.service.BaseService;
import org.springframework.stereotype.Service;

@Service
public class DoctorServiceInv extends BaseService<Doctor> {
    public DoctorServiceInv() {
        super(Doctor::new);
    }
}