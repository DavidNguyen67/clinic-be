package com.camel.clinic.service.services;

import com.camel.clinic.entity.ClinicService;
import com.camel.clinic.repository.ServicesRepository;
import com.camel.clinic.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ServicesServiceInv extends BaseService<ClinicService, ServicesRepository> {
    public ServicesServiceInv(ServicesRepository repository) {
        super(ClinicService::new, repository);
    }
}