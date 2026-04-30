package com.camel.clinic.service.appointment;

import com.camel.clinic.entity.Appointment;
import com.camel.clinic.repository.AppointmentRepository;
import com.camel.clinic.service.BaseService;
import jakarta.persistence.criteria.JoinType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

@Slf4j
@Service
public class AppointmentServiceInv extends BaseService<Appointment, AppointmentRepository> {

    public AppointmentServiceInv(AppointmentRepository repository) {
        super(Appointment::new, repository);
    }

    @Override
    protected Specification<Appointment> buildSpec(Map<String, Object> queryParams) {
        return Specification.<Appointment>unrestricted()
                .and(notDeleted())
                .and((root, query, cb) -> {
                    assert query != null;
                    if (!query.getResultType().equals(Long.class)) {
                        root.fetch("doctorProfile", JoinType.LEFT);
                        root.fetch("patientProfile", JoinType.LEFT);
                        root.fetch("specialty", JoinType.LEFT);
                        root.fetch("clinicService", JoinType.LEFT);
                        query.distinct(true);
                    }
                    return cb.conjunction();
                })
                .and(hasField("status", queryParams.get("status")))
                .and(hasNestedField("doctorProfile", "id", queryParams.get("doctorProfileId")))
                .and(hasNestedField("patientProfile", "id", queryParams.get("patientProfileId")))
                .and(appointmentOnDate((Date) queryParams.get("appointmentDate")));
    }

    private Specification<Appointment> appointmentOnDate(Date date) {
        if (date == null) return Specification.unrestricted();
        return (root, query, cb) -> {
            Calendar cal = Calendar.getInstance();

            cal.setTime(date);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            Date startOfDay = cal.getTime();

            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);
            Date endOfDay = cal.getTime();

            return cb.between(root.get("appointmentDate"), startOfDay, endOfDay);
        };
    }
}