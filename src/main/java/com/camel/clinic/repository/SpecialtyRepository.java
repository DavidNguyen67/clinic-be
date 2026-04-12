package com.camel.clinic.repository;

import com.camel.clinic.entity.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface SpecialtyRepository extends JpaRepository<Specialty, UUID>, JpaSpecificationExecutor<Specialty> {
}
