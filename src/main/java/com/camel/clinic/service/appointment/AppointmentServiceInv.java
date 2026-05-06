package com.camel.clinic.service.appointment;

import com.camel.clinic.dto.ApiPaged;
import com.camel.clinic.dto.appointment.ResponseAppointmentDto;
import com.camel.clinic.entity.Appointment;
import com.camel.clinic.repository.AppointmentRepository;
import com.camel.clinic.service.BaseService;
import com.camel.clinic.service.CommonService;
import jakarta.persistence.criteria.JoinType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AppointmentServiceInv extends BaseService<Appointment, AppointmentRepository> {

    public AppointmentServiceInv(AppointmentRepository repository) {
        super(Appointment::new, repository);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<?> list(Map<String, Object> queryParams) {
        ResponseEntity<?> base = super.list(queryParams);

        if (base.getBody() instanceof ApiPaged<?> paged) {
            List<ResponseAppointmentDto> data = paged.getData().stream()
                    .filter(e -> e instanceof Appointment)
                    .map(e -> ResponseAppointmentDto.from((Appointment) e))
                    .toList();

            return ResponseEntity.ok(ApiPaged.of(
                    data,
                    paged.getTotal(),
                    paged.getPage(),
                    paged.getSize(),
                    paged.getTotalPages()
            ));
        }

        return base;
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<?> retrieve(String id, String fields) {
        ResponseEntity<?> base = super.retrieve(id, fields);
        if (base.getBody() instanceof Appointment record) {
            return ResponseEntity.ok(ResponseAppointmentDto.from(record));
        }
        return base;
    }

    @Override
    public ResponseEntity<?> restore(String id) {
        ResponseEntity<?> base = super.restore(id);
        if (base.getBody() instanceof Appointment record) {
            return ResponseEntity.ok(ResponseAppointmentDto.from(record));
        }
        return base;
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
                .and(excludeId(CommonService.parseUuid(queryParams.get("excludeId"))))
                .and(fieldIn("status", queryParams.get("status"), Appointment.AppointmentStatus.class))
                .and(nestedFieldEqual("doctorProfile", "id", CommonService.parseUuid(queryParams.get("doctorProfileId"))))
                .and(nestedFieldEqual("patientProfile", "id", CommonService.parseUuid(queryParams.get("patientProfileId"))))
                .and(fieldOnDate("appointmentDate",
                        CommonService.parseToDate(
                                (String) queryParams.get("appointmentDate"))))
                .and(fieldBetweenDates("appointmentDate",
                        CommonService.parseToDate((String) queryParams.get("fromDate"), "HH:mm dd/MM/yyyy"),
                        CommonService.parseToDate((String) queryParams.get("toDate"), "HH:mm dd/MM/yyyy")
                ));
    }

    public boolean isExistAppointmentForDoctorAt(String doctorProfileId, Date appointmentDate, String excludeAppointmentId) {
        String dateStr = CommonService.formatDate(appointmentDate, "HH:mm dd/MM/yyyy");
        Date date = CommonService.parseToDate(dateStr, "HH:mm dd/MM/yyyy");
        Date toDate = new Date(date.getTime() + 59 * 60 * 1000); // Add 59 minutes to cover the same hour
        Date fromDate = new Date(date.getTime() - 59 * 60 * 1000); // Subtract 59 minutes to cover the same hour

        Map<String, Object> params = new HashMap<>();
        params.put("doctorProfileId", doctorProfileId);
        params.put("fromDate", CommonService.formatDate(fromDate, "HH:mm dd/MM/yyyy"));
        params.put("toDate", CommonService.formatDate(toDate, "HH:mm dd/MM/yyyy"));

        if (excludeAppointmentId != null) {
            params.put("excludeId", excludeAppointmentId);
        }

        long count = repository.count(buildSpec(params));

        return count > 0;
    }
}