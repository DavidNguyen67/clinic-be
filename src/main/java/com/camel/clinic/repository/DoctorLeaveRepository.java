package com.camel.clinic.repository;

import com.camel.clinic.entity.DoctorLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface DoctorLeaveRepository extends JpaRepository<DoctorLeave, UUID>, JpaSpecificationExecutor<DoctorLeave> {

    @Query("""
            SELECT dl FROM DoctorLeave dl
            WHERE dl.doctor.id = :doctorId
              AND dl.deletedAt IS NULL
            ORDER BY dl.leaveDate DESC
            """)
    List<DoctorLeave> findByDoctorId(@Param("doctorId") UUID doctorId);

    @Query("""
            SELECT dl FROM DoctorLeave dl
            WHERE dl.status = :status
              AND dl.deletedAt IS NULL
            ORDER BY dl.leaveDate DESC
            """)
    List<DoctorLeave> findByStatus(@Param("status") DoctorLeave.LeaveStatus status);

    @Query("""
            SELECT dl FROM DoctorLeave dl
            WHERE dl.doctor.id = :doctorId
              AND dl.leaveDate = :leaveDate
              AND dl.deletedAt IS NULL
            """)
    List<DoctorLeave> findByDoctorIdAndLeaveDate(
            @Param("doctorId") UUID doctorId,
            @Param("leaveDate") Date leaveDate
    );

    @Query("""
            SELECT dl FROM DoctorLeave dl
            WHERE dl.doctor.id = :doctorId
              AND dl.status = 'pending'
              AND dl.deletedAt IS NULL
            ORDER BY dl.leaveDate ASC
            """)
    List<DoctorLeave> findPendingByDoctorId(@Param("doctorId") UUID doctorId);

    @Query("""
            SELECT COUNT(l) > 0 FROM DoctorLeave l
            WHERE l.doctor.id  = :doctorId
              AND l.leaveDate = :date
              AND l.deletedAt IS NULL
            """)
    boolean existsLeaveOnDate(
            @Param("doctorId") UUID doctorId,
            @Param("date") Date date
    );

    @Query("""
            SELECT COUNT(l) > 0 FROM DoctorLeave l
            WHERE l.doctor.id  = :doctorId
              AND l.leaveDate = :date
              AND l.status = com.camel.clinic.entity.DoctorLeave.LeaveStatus.approved
              AND l.deletedAt IS NULL
            """)
    boolean existsApprovedLeaveOnDate(
            @Param("doctorId") UUID doctorId,
            @Param("date") Date date
    );

    boolean existsByDoctorIdAndLeaveDateAndStartTimeAndEndTimeAndStatusNot(
            UUID doctorId,
            Date leaveDate,
            Date startTime,
            Date endTime,
            DoctorLeave.LeaveStatus status
    );
}

