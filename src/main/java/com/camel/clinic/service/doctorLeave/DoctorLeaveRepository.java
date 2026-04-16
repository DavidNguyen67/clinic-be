package com.camel.clinic.service.doctorLeave;

import com.camel.clinic.entity.DoctorLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.UUID;

@Repository
public interface DoctorLeaveRepository extends JpaRepository<DoctorLeave, UUID>, JpaSpecificationExecutor<DoctorLeave> {

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
}