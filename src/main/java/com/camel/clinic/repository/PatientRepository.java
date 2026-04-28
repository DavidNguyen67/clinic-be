package com.camel.clinic.repository;

import com.camel.clinic.entity.PatientProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<PatientProfile, UUID>, JpaSpecificationExecutor<PatientProfile> {
    boolean existsByPatientCode(String patientCode);
}
