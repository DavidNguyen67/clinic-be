package com.camel.clinic.repository;

import com.camel.clinic.entity.DoctorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DoctorProfileRepository extends JpaRepository<DoctorProfile, UUID>, JpaSpecificationExecutor<DoctorProfile> {
    Optional<DoctorProfile> findByUserId(UUID userId);
}
