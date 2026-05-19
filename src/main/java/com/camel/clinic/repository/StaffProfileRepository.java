package com.camel.clinic.repository;

import com.camel.clinic.entity.StaffProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StaffProfileRepository extends JpaRepository<StaffProfile, UUID>, JpaSpecificationExecutor<StaffProfile> {
    Optional<StaffProfile> findByUserId(UUID userId);
}

