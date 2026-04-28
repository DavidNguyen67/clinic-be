package com.camel.clinic.repository;

import com.camel.clinic.entity.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID>, JpaSpecificationExecutor<Appointment> {

    /**
     * Lấy startTime của tất cả appointment không bị huỷ trong ngày.
     * So sánh theo date phần ngày (DB lưu TIMESTAMP).
     */
    @Query("""
            SELECT a.startTime
            FROM Appointment a
            WHERE a.doctor.id = :doctorId
              AND a.startTime >= :startOfDay
              AND a.startTime <  :endOfDay
              AND a.status NOT IN ('cancelled', 'no_show')
              AND a.deletedAt IS NULL
            """)
    List<Date> findBookedStartTimesOnDate(
            @Param("doctorId") UUID doctorId,
            @Param("startOfDay") Date startOfDay,
            @Param("endOfDay") Date endOfDay
    );

    @Query("""
            SELECT a FROM Appointment a
            LEFT JOIN FETCH a.doctor d
            LEFT JOIN FETCH a.patient p
            LEFT JOIN FETCH a.clinicService s
            WHERE a.patient.id = :patientId
              AND a.deletedAt IS NULL
            ORDER BY a.appointmentDate DESC, a.startTime DESC
            """)
    List<Appointment> findByPatientId(@Param("patientId") UUID patientId);

    @Query("""
            SELECT a FROM Appointment a
            WHERE a.doctor.id = :doctorId
              AND a.deletedAt IS NULL
            ORDER BY a.appointmentDate DESC, a.startTime DESC
            """)
    List<Appointment> findByDoctorId(@Param("doctorId") UUID doctorId);

    @Query("""
            SELECT a FROM Appointment a
            WHERE a.doctor.id = :doctorId
              AND a.status = :status
              AND a.deletedAt IS NULL
            ORDER BY a.appointmentDate DESC, a.startTime DESC
            """)
    List<Appointment> findByDoctorIdAndStatus(
            @Param("doctorId") UUID doctorId,
            @Param("status") Appointment.AppointmentStatus status
    );

    @Query("""
            SELECT a FROM Appointment a
            WHERE a.appointmentDate >= :fromDate
              AND a.appointmentDate < :toDate
              AND a.deletedAt IS NULL
            ORDER BY a.startTime ASC
            """)
    List<Appointment> findTodayAppointments(
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate
    );

    @Query("""
            SELECT a FROM Appointment a
                JOIN FETCH a.patient p
                JOIN FETCH p.user pu
                JOIN FETCH a.doctor d
                JOIN FETCH d.user du
                JOIN FETCH d.specialty ds
                JOIN FETCH a.clinicService cs
                JOIN FETCH cs.specialty css
            WHERE a.doctor.id = :doctorId
              AND a.appointmentDate >= :fromDate
              AND a.appointmentDate < :toDate
              AND a.deletedAt IS NULL
            ORDER BY a.startTime ASC
            """)
    List<Appointment> findTodayAppointmentsByDoctor(
            @Param("doctorId") UUID doctorId,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate
    );

    @Query("""
            SELECT a FROM Appointment a
            WHERE a.status = com.camel.clinic.entity.Appointment.AppointmentStatus.CHECKED_IN
              AND a.deletedAt IS NULL
            ORDER BY a.appointmentDate ASC, a.startTime ASC
            """)
    List<Appointment> findQueueAppointments();

    @Query("""
            SELECT a FROM Appointment a
            WHERE a.status = com.camel.clinic.entity.Appointment.AppointmentStatus.CONFIRMED
              AND a.startTime >= :fromTime
              AND a.startTime < :toTime
              AND a.deletedAt IS NULL
            ORDER BY a.startTime ASC
            """)
    List<Appointment> findConfirmedByStartTimeBetween(
            @Param("fromTime") Date fromTime,
            @Param("toTime") Date toTime
    );

    boolean existsByDoctorIdAndAppointmentDateAndStartTimeAndStatusInAndDeletedAtIsNull(
            UUID doctorId,
            Date appointmentDate,
            Date startTime,
            List<Appointment.AppointmentStatus> statuses
    );

    @EntityGraph(attributePaths = {
            "patient", "patient.user",
            "doctor", "doctor.user",
            "doctor.specialty",
            "clinicService", "clinicService.specialty"
    })
    Page<Appointment> findByPatientId(UUID patientId, Pageable pageable);

    @EntityGraph(attributePaths = {
            "patient", "patient.user",
            "doctor", "doctor.user",
            "doctor.specialty",
            "clinicService", "clinicService.specialty"
    })
    Page<Appointment> findByDoctorId(UUID doctorId, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {
            "patient", "patient.user",
            "doctor", "doctor.user",
            "doctor.specialty",
            "clinicService", "clinicService.specialty"
    })
    Page<Appointment> findAll(Pageable pageable);

    @Query("""
            SELECT COUNT(a)
            FROM Appointment a
            WHERE (CAST(:appointmentDate AS date) IS NULL OR a.appointmentDate = :appointmentDate)
              AND a.deletedAt IS NULL
            """)
    long countByAppointmentDateAndDeletedAtIsNull(@Param("appointmentDate") Date appointmentDate);

    long countByAppointmentDateGreaterThanEqualAndAppointmentDateLessThanAndDeletedAtIsNull(
            Date fromDate,
            Date toDate
    );

    @Query("""
            SELECT a.status, COUNT(a)
            FROM Appointment a
            WHERE a.deletedAt IS NULL
            GROUP BY a.status
            """)
    List<Object[]> countByStatusForActiveAppointments();

    //    findNextAvailableSlot
    @Query("""
            SELECT a.startTime
            FROM Appointment a
            WHERE a.doctor.id = :doctorId
            AND a.appointmentDate >= :fromDate
            AND a.appointmentDate < :toDate
            AND a.status NOT IN ('cancelled', 'no_show')
            AND a.deletedAt IS NULL
            ORDER BY a.appointmentDate ASC, a.startTime ASC
            """)
    List<Date> findBookedStartTimesBetween(
            @Param("doctorId") UUID doctorId,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate
    );

    @Query(
            value = """
                    SELECT a FROM Appointment a
                    JOIN FETCH a.patient p
                    JOIN FETCH p.user pu
                    JOIN FETCH a.doctor d
                    JOIN FETCH d.user du
                    JOIN FETCH d.specialty ds
                    JOIN FETCH a.clinicService cs
                    JOIN FETCH cs.specialty css
                    WHERE (:patientName IS NULL OR pu.fullName LIKE %:patientName%)
                      AND (CAST(:appointmentDate AS date) IS NULL OR a.appointmentDate = :appointmentDate)
                      AND a.deletedAt IS NULL
                    ORDER BY a.appointmentDate DESC, a.startTime DESC
                    """,
            countQuery = """
                    SELECT COUNT(a) FROM Appointment a
                    JOIN a.patient p
                    JOIN p.user pu
                    JOIN a.doctor d
                    JOIN d.user du
                    JOIN d.specialty ds
                    JOIN a.clinicService cs
                    JOIN cs.specialty css
                    WHERE (:patientName IS NULL OR pu.fullName LIKE %:patientName%)
                      AND (CAST(:appointmentDate AS date) IS NULL OR a.appointmentDate = :appointmentDate)
                      AND a.deletedAt IS NULL
                    """
    )
    Page<Appointment> findStaffAppointments(
            @Param("patientName") String patientName,
            @Param("appointmentDate") Date appointmentDate,
            Pageable pageable
    );

    @Query("""
            SELECT COUNT(a)
            FROM Appointment a
            WHERE a.status = :status
              AND (CAST(:appointmentDate AS date) IS NULL OR a.appointmentDate = :appointmentDate)
              AND a.deletedAt IS NULL
            """)
    long countStaffAppointmentsByStatus(
            @Param("status") Appointment.AppointmentStatus status,
            @Param("appointmentDate") Date appointmentDate
    );

    @EntityGraph(attributePaths = {
            "patient", "patient.user",
            "doctor", "doctor.user",
            "doctor.specialty",
            "clinicService", "clinicService.specialty"
    })
    @Query("""
            SELECT a FROM Appointment a
            WHERE a.patient.id = :patientId
              AND (CAST(:appointmentDate AS date) IS NULL OR a.appointmentDate = :appointmentDate)
              AND a.deletedAt IS NULL
            """)
    Page<Appointment> findByPatientIdAndDate(
            @Param("patientId") UUID patientId,
            @Param("appointmentDate") Date appointmentDate,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {
            "patient", "patient.user",
            "doctor", "doctor.user",
            "doctor.specialty",
            "clinicService", "clinicService.specialty"
    })
    @Query("""
            SELECT a FROM Appointment a
            WHERE a.doctor.id = :doctorId
              AND (CAST(:appointmentDate AS date) IS NULL OR a.appointmentDate = :appointmentDate)
              AND a.deletedAt IS NULL
            """)
    Page<Appointment> findByDoctorIdAndDate(
            @Param("doctorId") UUID doctorId,
            @Param("appointmentDate") Date appointmentDate,
            Pageable pageable
    );
}