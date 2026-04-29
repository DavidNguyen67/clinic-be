package com.camel.clinic.repository;

import com.camel.clinic.entity.ClinicService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ServicesRepository extends JpaRepository<ClinicService, UUID>, JpaSpecificationExecutor<ClinicService> {
}
