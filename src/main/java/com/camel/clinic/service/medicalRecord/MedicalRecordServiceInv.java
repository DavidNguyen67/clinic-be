package com.camel.clinic.service.medicalRecord;

import com.camel.clinic.entity.MedicalRecord;
import com.camel.clinic.repository.MedicalRecordRepository;
import com.camel.clinic.service.BaseService;
import com.camel.clinic.service.CommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class MedicalRecordServiceInv extends BaseService<MedicalRecord, MedicalRecordRepository> {
    public MedicalRecordServiceInv(MedicalRecordRepository repository) {
        super(MedicalRecord::new, repository);
    }

    @Override
    protected Specification<MedicalRecord> buildSpec(Map<String, Object> queryParams) {
        return Specification.<MedicalRecord>unrestricted()
                .and(notDeleted())
                .and(nestedFieldEqual("doctorProfile", "id", CommonService.parseUuid(queryParams.get("doctorProfileId"))))
                .and(nestedFieldEqual("patientProfile", "id", CommonService.parseUuid(queryParams.get("patientProfileId"))));
    }
}