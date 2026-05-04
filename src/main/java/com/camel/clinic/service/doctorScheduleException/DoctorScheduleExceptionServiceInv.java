package com.camel.clinic.service.doctorScheduleException;

import com.camel.clinic.entity.DoctorProfile;
import com.camel.clinic.entity.DoctorScheduleException;
import com.camel.clinic.repository.DoctorScheduleExceptionRepository;
import com.camel.clinic.service.BaseService;
import com.camel.clinic.service.CommonService;
import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.JoinType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Slf4j
@Service
public class DoctorScheduleExceptionServiceInv extends BaseService<DoctorScheduleException, DoctorScheduleExceptionRepository> {

    public DoctorScheduleExceptionServiceInv(DoctorScheduleExceptionRepository repository) {
        super(DoctorScheduleException::new, repository);
    }

    @Override
    protected Specification<DoctorScheduleException> buildSpec(Map<String, Object> queryParams) {
        return Specification.<DoctorScheduleException>unrestricted()
                .and(notDeleted())
                .and((root, query, cb) -> {
                    assert query != null;
                    if (!query.getResultType().equals(Long.class)) {
                        Fetch<DoctorScheduleException, DoctorProfile> doctorFetch = root.fetch("doctorProfile", JoinType.LEFT);
                        doctorFetch.fetch("user", JoinType.LEFT);
                        doctorFetch.fetch("specialty", JoinType.LEFT);
                        query.distinct(true);
                    }
                    return cb.conjunction();
                })
                .and(fieldEquals("type", CommonService.parseEnum(DoctorScheduleException.ExceptionType.class, queryParams.get("type"))))
                .and(nestedFieldEqual("doctorProfile", "id", CommonService.parseUuid(queryParams.get("doctorId"))))
                .and(fieldOnDate("exceptionDate", CommonService.parseToDate((String) queryParams.get("exceptionDate"), "dd/MM/yyyy")))
                .and(fieldBetweenDates("exceptionDate",
                        CommonService.parseToDate((String) queryParams.get("from")), CommonService.parseToDate((String) queryParams.get("to"))));

    }

    public boolean isDoctorAvailable(String doctorProfileId, Date exceptionDate) {
        long count = repository.count(buildSpec(Map.of(
                "doctorId", doctorProfileId,
                "exceptionDate", CommonService.formatDate(exceptionDate, "dd/MM/yyyy"),
                "type", DoctorScheduleException.ExceptionType.EXTRA
        )));

        return count <= 0;
    }
}