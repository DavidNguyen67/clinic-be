package com.camel.clinic.service.appointment;

import com.camel.clinic.dto.ApiPaged;
import com.camel.clinic.dto.appointment.AppointmentResponseDto;
import com.camel.clinic.entity.Appointment;
import com.camel.clinic.repository.AppointmentRepository;
import com.camel.clinic.service.BaseService;
import jakarta.persistence.criteria.JoinType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
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
            List<AppointmentResponseDto> data = paged.getData().stream()
                    .filter(e -> e instanceof Appointment)
                    .map(e -> AppointmentResponseDto.from((Appointment) e))
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
            return ResponseEntity.ok(AppointmentResponseDto.from(record));
        }
        return base;
    }

    @Override
    public ResponseEntity<?> restore(String id) {
        ResponseEntity<?> base = super.restore(id);
        if (base.getBody() instanceof Appointment record) {
            return ResponseEntity.ok(AppointmentResponseDto.from(record));
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
                .and(fieldIn("status", queryParams.get("status"), Appointment.AppointmentStatus.class))
                .and(hasNestedField("doctorProfile", "id", queryParams.get("doctorProfileId")))
                .and(hasNestedField("patientProfile", "id", queryParams.get("patientProfileId")))
                .and(fieldOnDate("appointmentDate", (Date) queryParams.get("appointmentDate")));
    }
}