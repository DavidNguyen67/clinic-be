package com.camel.clinic.service.patientProfile;

import com.camel.clinic.entity.PatientProfile;
import com.camel.clinic.repository.PatientProfileRepository;
import com.camel.clinic.service.BaseService;
import com.camel.clinic.service.CommonService;
import jakarta.persistence.criteria.JoinType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class PatientProfileServiceInv extends BaseService<PatientProfile, PatientProfileRepository> {
    public PatientProfileServiceInv(PatientProfileRepository repository) {
        super(PatientProfile::new, repository);
    }

    public List<PatientProfile> findInIds(List<String> ids) {
        return repository.findAll(buildSpec(Map.of("ids", ids)));
    }

    @Override
    protected Specification<PatientProfile> buildSpec(Map<String, Object> queryParams) {
        List<String> ids = (List<String>) queryParams.get("ids");
        List<UUID> uuids = CommonService.parseToList(ids, UUID::fromString);

        return Specification.<PatientProfile>unrestricted()
                .and(notDeleted())
                .and(multiFieldIn(uuids, new String[]{"id"}))
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