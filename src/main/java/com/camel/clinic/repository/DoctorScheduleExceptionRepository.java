package com.camel.clinic.repository;

import com.camel.clinic.entity.DoctorScheduleException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DoctorScheduleExceptionRepository extends JpaRepository<DoctorScheduleException, UUID>, JpaSpecificationExecutor<DoctorScheduleException> {
}