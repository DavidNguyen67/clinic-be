package com.camel.clinic.repository;

import com.camel.clinic.entity.ClinicService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ClinicServiceRepository extends JpaRepository<ClinicService, UUID>, JpaSpecificationExecutor<ClinicService> {
    @EntityGraph(attributePaths = {"specialty"})
    Page<ClinicService> findAll(Pageable pageable);
}
