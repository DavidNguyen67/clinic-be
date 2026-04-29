package com.camel.clinic.repository;

import com.camel.clinic.entity.Specialty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface SpecialtyRepository extends JpaRepository<Specialty, UUID>, JpaSpecificationExecutor<Specialty> {
    @Query(
            value = "SELECT s" +
                    " FROM Specialty s" +
                    " LEFT JOIN s.doctorProfiles d" +
                    " LEFT JOIN s.services sv" +
                    " WHERE s.isActive = true" +
                    " AND s.deletedAt IS NULL" +
                    " AND (:serviceId IS NULL OR sv.id = :serviceId)" +
                    " GROUP BY s.id, s.name, s.slug, s.description, s.image," +
                    "          s.displayOrder, s.isActive, s.specialtyType" +
                    " ORDER BY s.displayOrder ASC",
            countQuery =
                    "SELECT COUNT(DISTINCT s) FROM Specialty s" +
                            " LEFT JOIN s.services sv" +
                            " WHERE s.isActive = true" +
                            " AND s.deletedAt IS NULL" +
                            " AND (:serviceId IS NULL OR sv.id = :serviceId)"
    )
    Page<Specialty> getAll(
            Pageable pageable,
            @Param("serviceId") UUID serviceId
    );
}
