package com.camel.clinic.service.appointment;

import com.camel.clinic.dto.ApiPaged;
import com.camel.clinic.dto.DateRange;
import com.camel.clinic.dto.appointment.AppointmentStatisticsDto;
import com.camel.clinic.dto.appointment.ResponseAppointmentDto;
import com.camel.clinic.entity.Appointment;
import com.camel.clinic.entity.Invoice;
import com.camel.clinic.entity.Specialty;
import com.camel.clinic.repository.AppointmentRepository;
import com.camel.clinic.repository.InvoiceRepository;
import com.camel.clinic.service.BaseService;
import com.camel.clinic.service.CommonService;
import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.JoinType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
public class AppointmentServiceInv extends BaseService<Appointment, AppointmentRepository> {
    private final InvoiceRepository invoiceRepository;

    public AppointmentServiceInv(AppointmentRepository repository, InvoiceRepository invoiceRepository) {
        super(Appointment::new, repository);
        this.invoiceRepository = invoiceRepository;
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
            List<Invoice> invoices = invoiceRepository.findByAppointmentId(record.getId())
                    .stream().toList();
            ResponseAppointmentDto dto = ResponseAppointmentDto.from(record);
            dto.setInvoices(invoices);
            return ResponseEntity.ok(dto);
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

    public ResponseEntity<?> calculatePatientStatistics(Map<String, Object> queryParams) {
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
        cal.set(Calendar.MILLISECOND, 999);
        Date monthEnd = cal.getTime();

        cal.setTime(inputDate);
        Date startDate = cal.getTime();

        Date endDate = buildNextWeekEndTime(cal);

        cal.setTime(inputDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        Date todayStart = cal.getTime();

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.MILLISECOND, 999);
        Date todayEnd = cal.getTime();

        Map<String, Object> todayParams = buildAppointmentTodayAndUpcomingParams(todayStart, todayEnd, queryParams);
        long todayCount = repository.count(buildSpec(todayParams));

        Map<String, Object> upcomingParams = buildAppointmentTodayAndUpcomingParams(startDate, endDate, queryParams);
        long upcomingCount = repository.count(buildSpec(upcomingParams));

        Map<String, Object> monthParams = new HashMap<>(queryParams);
        monthParams.put("fromDate", CommonService.formatDate(monthStart, "HH:mm dd/MM/yyyy"));
        monthParams.put("toDate", CommonService.formatDate(monthEnd, "HH:mm dd/MM/yyyy"));
        monthParams.remove("appointmentDate");

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

    private Date buildNextWeekEndTime(Calendar cal) {
        cal.add(Calendar.DAY_OF_MONTH, 7);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);

        return cal.getTime();
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
                .and(multiFieldNotIn(parseEnumList(queryParams.get("excludeStatus"), Appointment.AppointmentStatus.class)
                        , new String[]{"status"}))
                .and(multiFieldIn(parseEnumList(queryParams.get("status"), Appointment.AppointmentStatus.class)
                        , new String[]{"status"}))
                .and(excludeId(CommonService.parseToUuid(queryParams.get("excludeId"))))
                .and(fieldIn("bookingType", CommonService.parseToEnum(Appointment.BookingType.class, queryParams.get("bookingType")), Appointment.BookingType.class))
                .and(nestedFieldEqual("doctorProfile", "id", CommonService.parseToUuid(queryParams.get("doctorProfileId"))))
                .and(nestedFieldEqual("patientProfile", "id", CommonService.parseToUuid(queryParams.get("patientProfileId"))))
                .and(fieldOnDate("appointmentDate",
                        CommonService.parseToDate(
                                (String) queryParams.get("appointmentDate"))))
                .and(multiFieldBetweenDates(
                        CommonService.parseToDate((String) queryParams.get("fromDate"), "HH:mm dd/MM/yyyy"),
                        CommonService.parseToDate((String) queryParams.get("toDate"), "HH:mm dd/MM/yyyy"),
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

    private Map<String, Object> buildAppointmentTodayAndUpcomingParams(Date startDate, Date endDate, Map<String, Object> baseParams) {
        Map<String, Object> queryParams = new HashMap<>(baseParams);
        queryParams.put("fromDate", CommonService.formatDate(startDate, "HH:mm dd/MM/yyyy"));
        queryParams.put("toDate", CommonService.formatDate(endDate, "HH:mm dd/MM/yyyy"));
        queryParams.put("excludeStatus",
                List.of(Appointment.AppointmentStatus.COMPLETED.name(),
                        Appointment.AppointmentStatus.CANCELLED.name(),
                        Appointment.AppointmentStatus.NO_SHOW.name())
        );
        queryParams.remove("appointmentDate");

        return queryParams;
    }

    public ResponseEntity<?> retrieveNext(Map<String, Object> queryParams) {
        String inputDateStr = (String) queryParams.get("appointmentDate");
        Date inputDate = inputDateStr != null
                ? CommonService.parseToDate(inputDateStr, "HH:mm dd/MM/yyyy")
                : new Date();

        Calendar cal = Calendar.getInstance();
        Date endDate = buildNextWeekEndTime(cal);

        Map<String, Object> params = new HashMap<>(queryParams);
        params.put("fromDate", CommonService.formatDate(inputDate, "HH:mm dd/MM/yyyy"));
        params.put("toDate", CommonService.formatDate(endDate, "HH:mm dd/MM/yyyy"));
        params.put("status", Appointment.AppointmentStatus.CONFIRMED.name());

        Specification<Appointment> spec = buildSpec(params);

        return repository.findAll(spec, Sort.by(Sort.Direction.ASC, "appointmentDate"))
                .stream()
                .findFirst()
                .map(appointment -> ResponseEntity.ok(ResponseAppointmentDto.from(appointment)))
                .orElse(ResponseEntity.noContent().build());
    }


    private Map<String, Object> buildRangeParams(Map<String, Object> base, DateRange range) {
        Map<String, Object> p = new HashMap<>(base);
        p.put("fromDate", CommonService.formatDate(range.getStart(), "HH:mm dd/MM/yyyy"));
        p.put("toDate", CommonService.formatDate(range.getEnd(), "HH:mm dd/MM/yyyy"));
        p.remove("appointmentDate");
        return p;
    }

    public ResponseEntity<?> calculateDoctorStatistics(Map<String, Object> queryParams) {
        // ── 1. Resolve target date ───────────────────────────────────────────────
        String inputDateStr = (String) queryParams.get("appointmentDate");
        Date inputDate = inputDateStr != null
                ? CommonService.parseToDate(inputDateStr, "dd/MM/yyyy")
                : CommonService.getCurrentDate();

        // ── 2. Build date ranges ─────────────────────────────────────────────────
        DateRange currentMonth = CommonService.buildMonthRange(inputDate);
        DateRange lastMonth = CommonService.buildLastMonthRange(inputDate);

        // ── 3. Query current month ───────────────────────────────────────────────
        Map<String, Object> currentParams = buildRangeParams(queryParams, currentMonth);
        long currentCompleted = countByStatus(Appointment.AppointmentStatus.COMPLETED, currentParams);
        long currentCancelled = countByStatus(Appointment.AppointmentStatus.CANCELLED, currentParams);
        // BUG FIX: was COMPLETED — should be PENDING
        long currentPending = countByStatus(Appointment.AppointmentStatus.PENDING, currentParams);
        long currentTotal = repository.count(buildSpec(currentParams));

        // ── 4. Query last month ──────────────────────────────────────────────────
        Map<String, Object> lastParams = buildRangeParams(queryParams, lastMonth);
        long lastCompleted = countByStatus(Appointment.AppointmentStatus.COMPLETED, lastParams);
        long lastCancelled = countByStatus(Appointment.AppointmentStatus.CANCELLED, lastParams);
        long lastPending = countByStatus(Appointment.AppointmentStatus.PENDING, lastParams);
        long lastTotal = repository.count(buildSpec(lastParams));

        // ── 5. Assemble response ─────────────────────────────────────────────────
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalAppointments", buildStat(currentTotal, lastTotal));
        stats.put("completed", buildStat(currentCompleted, lastCompleted));
        stats.put("cancelled", buildStat(currentCancelled, lastCancelled));
        stats.put("pending", buildStat(currentPending, lastPending));

        return ResponseEntity.ok(stats);
    }

    private Map<String, Object> buildStat(long current, long last) {
        double deltaPercent = last == 0 ? 0.0 : (double) (current - last) / last * 100;
        Map<String, Object> m = new HashMap<>();
        m.put("value", current);
        m.put("lastMonth", last);
        m.put("delta", current - last);          // e.g. +13 / -3
        m.put("deltaPercent", Math.round(deltaPercent * 10) / 10.0); // e.g. +11.0 %
        return m;
    }

}