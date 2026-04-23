package com.camel.clinic.repository;

import com.camel.clinic.entity.DoctorSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, UUID>, JpaSpecificationExecutor<DoctorSchedule> {

    @Query("""
            SELECT s FROM DoctorSchedule s
            WHERE s.doctor.id = :doctorId
              AND s.dayOfWeek = :dayOfWeek
              AND s.isActive = true
              AND s.deletedAt IS NULL
            """)
    List<DoctorSchedule> findActiveByDoctorIdAndDayOfWeek(
            @Param("doctorId") UUID doctorId,
            @Param("dayOfWeek") int dayOfWeek
    );

    @Query("""
            SELECT s FROM DoctorSchedule s
            WHERE s.doctor.id = :doctorId
              AND s.deletedAt IS NULL
            ORDER BY s.dayOfWeek ASC, s.startTime ASC
            """)
    List<DoctorSchedule> findByDoctorId(@Param("doctorId") UUID doctorId);

    @Query("""
            SELECT s FROM DoctorSchedule s
            WHERE s.doctor.id = :doctorId
              AND s.isActive = true
              AND s.deletedAt IS NULL
            ORDER BY s.dayOfWeek ASC, s.startTime ASC
            """)
    List<DoctorSchedule> findActiveByDoctorId(@Param("doctorId") UUID doctorId);

    @Query("""
            SELECT COUNT(s) FROM DoctorSchedule s
            WHERE s.doctor.id = :doctorId
              AND s.dayOfWeek = :dayOfWeek
              AND s.deletedAt IS NULL
            """)
    long countByDoctorIdAndDayOfWeek(
            @Param("doctorId") UUID doctorId,
            @Param("dayOfWeek") int dayOfWeek
    );

    @Query("""
            SELECT s FROM DoctorSchedule s
            WHERE s.doctor.id = :doctorId
              AND s.dayOfWeek = :dayOfWeek
              AND s.deletedAt IS NULL
            """)
    List<DoctorSchedule> findOverlappingSchedules(
            @Param("doctorId") UUID doctorId,
            @Param("dayOfWeek") int dayOfWeek);

    @Query("""
            SELECT s FROM DoctorSchedule s
            WHERE s.doctor.id = :doctorId
              AND s.dayOfWeek = :dayOfWeek
              AND s.isActive = true
              AND s.deletedAt IS NULL
            ORDER BY s.startTime ASC
            """)
    Optional<DoctorSchedule> findTodaySchedulesByDoctorId(
            @Param("doctorId") UUID doctorId,
            @Param("dayOfWeek") int dayOfWeek);

    List<DoctorSchedule> findByDoctorIdAndDayOfWeekIn(UUID doctorId, List<Integer> dayOfWeeks);
}