package com.camel.clinic.service.patientProfile;

import com.camel.clinic.entity.PatientProfile;
import com.camel.clinic.repository.PatientProfileRepository;
import com.camel.clinic.service.BaseService;
import jakarta.persistence.criteria.JoinType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class PatientProfileServiceInv extends BaseService<PatientProfile, PatientProfileRepository> {
    public PatientProfileServiceInv(PatientProfileRepository repository) {
        super(PatientProfile::new, repository);
    }

    @Override
    protected Specification<PatientProfile> buildSpec(Map<String, Object> queryParams) {
        return Specification.<PatientProfile>unrestricted()
                .and(notDeleted())
                .and((root, query, cb) -> {
                    assert query != null;
                    if (!query.getResultType().equals(Long.class)) {
                        root.fetch("user", JoinType.LEFT);
                        query.distinct(true);
                    }
                    return cb.conjunction();
                });
    }
}