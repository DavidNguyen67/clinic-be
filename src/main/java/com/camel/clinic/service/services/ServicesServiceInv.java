package com.camel.clinic.service.services;

import com.camel.clinic.entity.ClinicService;
import com.camel.clinic.repository.ServicesRepository;
import com.camel.clinic.service.BaseService;
import com.camel.clinic.service.CommonService;
import jakarta.persistence.criteria.JoinType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class ServicesServiceInv extends BaseService<ClinicService, ServicesRepository> {
    public ServicesServiceInv(ServicesRepository repository) {
        super(ClinicService::new, repository);
    }

    @Override
    protected Specification<ClinicService> buildSpec(Map<String, Object> queryParams) {
        return Specification.<ClinicService>unrestricted()
                .and(notDeleted())
                .and((root, query, cb) -> {
                    assert query != null;
                    if (!query.getResultType().equals(Long.class)) {
                        root.fetch("specialty", JoinType.LEFT);
                        query.distinct(true);
                    }
                    return cb.conjunction();
                })
                .and(fieldEquals("isFeatured", CommonService.parseBoolean(queryParams.get("isFeatured"))))
                .and(fieldEquals("isActive", CommonService.parseBoolean(queryParams.get("isActive"))))
                .and(fieldLike("name", (String) queryParams.get("name")))
                .and(fieldLike("slug", (String) queryParams.get("slug")))
                .and(nestedFieldEqual("specialty", "id", CommonService.parseUuid(queryParams.get("specialtyId"))));
    }
}