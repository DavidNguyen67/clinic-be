package com.camel.clinic.service.doctorScheduleException;

import com.camel.clinic.entity.DoctorScheduleException;
import com.camel.clinic.repository.DoctorScheduleExceptionRepository;
import com.camel.clinic.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DoctorScheduleExceptionServiceInv extends BaseService<DoctorScheduleException, DoctorScheduleExceptionRepository> {
    public DoctorScheduleExceptionServiceInv(DoctorScheduleExceptionRepository repository) {
        super(DoctorScheduleException::new, repository);
    }
}