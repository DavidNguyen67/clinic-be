package com.camel.clinic.service.doctorProfile;

import com.camel.clinic.entity.DoctorProfile;
import com.camel.clinic.repository.DoctorProfileRepository;
import com.camel.clinic.service.BaseService;
import com.camel.clinic.service.CommonService;
import jakarta.persistence.criteria.JoinType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class DoctorProfileServiceInv extends BaseService<DoctorProfile, DoctorProfileRepository> {

    public DoctorProfileServiceInv(DoctorProfileRepository repository) {
        super(DoctorProfile::new, repository);
    }

    @Override
    protected Specification<DoctorProfile> buildSpec(Map<String, Object> queryParams) {
        return Specification.<DoctorProfile>unrestricted()
                .and(notDeleted())
                .and((root, query, cb) -> {
                    assert query != null;
                    if (!query.getResultType().equals(Long.class)) {
                        root.fetch("user", JoinType.LEFT);
                        root.fetch("specialty", JoinType.LEFT);
                        query.distinct(true);
                    }
                    return cb.conjunction();
                })
                .and(fieldEquals("isFeatured", CommonService.parseBoolean(queryParams.get("isFeatured"))))
                .and(nestedFieldLike("user", "fullName", (String) queryParams.get("fullName")))
                .and(nestedFieldLike("specialty", "name", (String) queryParams.get("specialtyName")))
                .and(nestedFieldEqual("specialty", "id", CommonService.parseUuid(queryParams.get("specialtyId"))));
    }

}