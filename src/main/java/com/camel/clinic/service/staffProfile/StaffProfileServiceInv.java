package com.camel.clinic.service.staffProfile;

import com.camel.clinic.entity.StaffProfile;
import com.camel.clinic.repository.StaffProfileRepository;
import com.camel.clinic.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StaffProfileServiceInv extends BaseService<StaffProfile, StaffProfileRepository> {
    public StaffProfileServiceInv(StaffProfileRepository repository) {
        super(StaffProfile::new, repository);
    }
}