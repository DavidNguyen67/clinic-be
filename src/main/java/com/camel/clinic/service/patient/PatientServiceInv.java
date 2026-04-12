package com.camel.clinic.service.patient;

import com.camel.clinic.entity.Patient;
import com.camel.clinic.repository.PatientRepository;
import com.camel.clinic.service.BaseService;
import org.springframework.stereotype.Service;

@Service
public class PatientServiceInv extends BaseService<Patient, PatientRepository> {
    public PatientServiceInv(PatientRepository repository) {
        super(Patient::new, repository);
    }
}