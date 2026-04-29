package com.camel.clinic.service.doctorProfile;

import com.camel.clinic.entity.DoctorProfile;
import com.camel.clinic.repository.DoctorProfileRepository;
import com.camel.clinic.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DoctorProfileServiceInv extends BaseService<DoctorProfile, DoctorProfileRepository> {
    public DoctorProfileServiceInv(DoctorProfileRepository repository) {
        super(DoctorProfile::new, repository);
    }
}