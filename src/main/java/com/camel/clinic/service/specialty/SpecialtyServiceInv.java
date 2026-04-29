package com.camel.clinic.service.specialty;

import com.camel.clinic.entity.Specialty;
import com.camel.clinic.repository.SpecialtyRepository;
import com.camel.clinic.service.BaseService;
import com.camel.clinic.service.CommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class SpecialtyServiceInv extends BaseService<Specialty, SpecialtyRepository> {
    private final CommonService commonService;

    public SpecialtyServiceInv(SpecialtyRepository repository, CommonService commonService) {
        super(Specialty::new, repository);
        this.commonService = commonService;
    }

    @Override
    protected Specification<Specialty> buildSpec(Map<String, Object> queryParams) {
        return Specification.<Specialty>unrestricted()
                .and(notDeleted())
                .and(hasField("isActive", commonService.parseBoolean(queryParams.get("isActive"))));
    }
}