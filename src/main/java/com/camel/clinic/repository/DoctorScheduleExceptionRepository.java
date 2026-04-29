package com.camel.clinic.repository;

import com.camel.clinic.entity.DoctorScheduleException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DoctorScheduleExceptionRepository extends JpaRepository<DoctorScheduleException, UUID>, JpaSpecificationExecutor<DoctorScheduleException> {
    @Query("SELECT dse" +
            " FROM DoctorScheduleException dse" +
            " WHERE dse.doctorProfile.id = :doctorProfileId" +
            " AND dse.deletedAt IS NULL" +
            " ORDER BY dse.exceptionDate ASC")
    Page<DoctorScheduleException> findAll(Pageable pageable, @Param("doctorProfileId") UUID doctorProfileId);
}