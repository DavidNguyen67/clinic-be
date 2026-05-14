package com.camel.clinic.service.appointment;

import com.camel.clinic.dto.ApiPaged;
import com.camel.clinic.dto.appointment.AppointmentStatisticsDto;
import com.camel.clinic.dto.appointment.ResponseAppointmentDto;
import com.camel.clinic.entity.Appointment;
import com.camel.clinic.entity.Specialty;
import com.camel.clinic.repository.AppointmentRepository;
import com.camel.clinic.service.BaseService;
import com.camel.clinic.service.CommonService;
import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.JoinType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

    public ResponseEntity<?> calculateStatistics(Map<String, Object> queryParams) {
        String inputDateStr = (String) queryParams.get("appointmentDate");
        Date inputDate = inputDateStr != null
                ? CommonService.parseToDate(inputDateStr, "dd/MM/yyyy")
                : CommonService.getCurrentDate();

        Calendar cal = Calendar.getInstance();
        cal.setTime(inputDate);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date monthStart = cal.getTime();

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        Date monthEnd = cal.getTime();

        cal.setTime(inputDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        Date todayStart = cal.getTime();

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        Date todayEnd = cal.getTime();

        Map<String, Object> monthParams = new HashMap<>(queryParams);
        monthParams.put("fromDate", CommonService.formatDate(monthStart, "HH:mm dd/MM/yyyy"));
        monthParams.put("toDate", CommonService.formatDate(monthEnd, "HH:mm dd/MM/yyyy"));
        monthParams.remove("appointmentDate");

        Map<String, Object> todayParams = new HashMap<>(queryParams);
        todayParams.put("fromDate", CommonService.formatDate(todayStart, "HH:mm dd/MM/yyyy"));
        todayParams.put("toDate", CommonService.formatDate(todayEnd, "HH:mm dd/MM/yyyy"));
        todayParams.remove("appointmentDate");
        long todayCount = repository.count(buildSpec(todayParams));

        Date tomorrow = new Date(todayEnd.getTime() + 60_000L);
        Map<String, Object> upcomingParams = new HashMap<>(queryParams);
        upcomingParams.put("fromDate", CommonService.formatDate(tomorrow, "HH:mm dd/MM/yyyy"));
        upcomingParams.put("toDate", CommonService.formatDate(monthEnd, "HH:mm dd/MM/yyyy"));
        upcomingParams.remove("appointmentDate");
        long upcomingCount = repository.count(buildSpec(upcomingParams));

        long pendingCount = countByStatus(Appointment.AppointmentStatus.PENDING, monthParams);
        long confirmedCount = countByStatus(Appointment.AppointmentStatus.CONFIRMED, monthParams);
        long checkedInCount = countByStatus(Appointment.AppointmentStatus.CHECKED_IN, monthParams);
        long inProgressCount = countByStatus(Appointment.AppointmentStatus.IN_PROGRESS, monthParams);
        long completedCount = countByStatus(Appointment.AppointmentStatus.COMPLETED, monthParams);
        long cancelledCount = countByStatus(Appointment.AppointmentStatus.CANCELLED, monthParams);
        long noShowCount = countByStatus(Appointment.AppointmentStatus.NO_SHOW, monthParams);

        AppointmentStatisticsDto dto = AppointmentStatisticsDto.builder()
                .todayCount(todayCount)
                .upcomingCount(upcomingCount)
                .pendingCount(pendingCount)
                .confirmedCount(confirmedCount)
                .checkedInCount(checkedInCount)
                .inProgressCount(inProgressCount)
                .completedCount(completedCount)
                .cancelledCount(cancelledCount)
                .noShowCount(noShowCount)
                .build();

        return ResponseEntity.ok(dto);
    }

    private long countByStatus(Appointment.AppointmentStatus status, Map<String, Object> baseParams) {
        Map<String, Object> params = new HashMap<>(baseParams);
        params.put("status", status.name());
        return repository.count(buildSpec(params));
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

                        Fetch<Appointment, Specialty> specialtyFetch = root.fetch("specialty", JoinType.LEFT);
                        specialtyFetch.fetch("services", JoinType.LEFT);

                        root.fetch("clinicService", JoinType.LEFT);
                        query.distinct(true);
                    }
                    return cb.conjunction();
                })
                .and(excludeId(CommonService.parseToUuid(queryParams.get("excludeId"))))
                .and(fieldIn("status", CommonService.parseToEnum(Appointment.AppointmentStatus.class, queryParams.get("status")), Appointment.AppointmentStatus.class))
                .and(nestedFieldEqual("doctorProfile", "id", CommonService.parseToUuid(queryParams.get("doctorProfileId"))))
                .and(nestedFieldEqual("patientProfile", "id", CommonService.parseToUuid(queryParams.get("patientProfileId"))))
                .and(fieldOnDate("appointmentDate",
                        CommonService.parseToDate(
                                (String) queryParams.get("appointmentDate"))))
                .and(multiFieldBetweenDates(
                        CommonService.parseToDate((String) queryParams.get("fromDate"), "dd/MM/yyyy"),
                        CommonService.parseToDate((String) queryParams.get("toDate"), "dd/MM/yyyy"),
                        new String[]{"appointmentDate"})
                )
                .and(keywordSpec(
                        (String) queryParams.get("keyword"),
                        new String[][]{
                                {"doctorProfile", "user", "fullName"},
                                {"specialty", "name"}
                        }
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