package com.camel.clinic.service.patientProfile;

import com.camel.clinic.entity.PatientProfile;
import com.camel.clinic.repository.PatientProfileRepository;
import com.camel.clinic.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PatientProfileServiceInv extends BaseService<PatientProfile, PatientProfileRepository> {
    public PatientProfileServiceInv(PatientProfileRepository repository) {
        super(PatientProfile::new, repository);
    }
}