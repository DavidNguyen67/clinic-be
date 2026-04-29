package com.camel.clinic.repository;

import com.camel.clinic.entity.ClinicService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ServicesRepository extends JpaRepository<ClinicService, UUID>, JpaSpecificationExecutor<ClinicService> {
    @Query(
            value = "SELECT s" +
                    " FROM ClinicService s" +
                    " LEFT JOIN s.specialty sp" +
                    " WHERE s.isActive = true" +
                    " AND s.deletedAt IS NULL",
            countQuery =
                    "SELECT COUNT(s) FROM ClinicService s" +
                            " WHERE s.isActive = true" +
                            " AND s.deletedAt IS NULL"
    )
    Page<ClinicService> getAll(Pageable pageable);
}
