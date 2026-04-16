package com.camel.clinic.repository;

import com.camel.clinic.entity.Appointment;
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
              AND a.status NOT IN ('cancelled', 'rejected')
              AND a.deletedAt IS NULL
            """)
    List<Date> findBookedStartTimesOnDate(
            @Param("doctorId") UUID doctorId,
            @Param("startOfDay") Date startOfDay,
            @Param("endOfDay") Date endOfDay
    );
}