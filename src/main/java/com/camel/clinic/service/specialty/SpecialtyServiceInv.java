package com.camel.clinic.service.specialty;

import com.camel.clinic.entity.Specialty;
import com.camel.clinic.repository.SpecialtyRepository;
import com.camel.clinic.service.BaseService;
import org.springframework.stereotype.Service;

@Service
public class SpecialtyServiceInv extends BaseService<Specialty, SpecialtyRepository> {
    public SpecialtyServiceInv(SpecialtyRepository repository) {
        super(Specialty::new, repository);
    }
}