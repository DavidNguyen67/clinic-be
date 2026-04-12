package com.camel.clinic.repository;

import com.camel.clinic.dto.SpecialtyWithDoctorCountDTO;
import com.camel.clinic.entity.Specialty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface SpecialtyRepository extends JpaRepository<Specialty, UUID>, JpaSpecificationExecutor<Specialty> {
    @Query(
            value = "SELECT new com.camel.clinic.dto.SpecialtyWithDoctorCountDTO(" +
                    "s.id, s.name, s.slug, s.description, s.image, " +
                    "s.displayOrder, s.isActive, s.specialtyType, COUNT(d))" +
                    " FROM Specialty s LEFT JOIN s.doctors d" +
                    " WHERE s.isActive = true" +
                    " GROUP BY s.id, s.name, s.slug, s.description, s.image," +
                    "          s.displayOrder, s.isActive, s.specialtyType" +
                    " ORDER BY s.displayOrder ASC",
            countQuery = "SELECT COUNT(DISTINCT s) FROM Specialty s WHERE s.isActive = true"
    )
    Page<SpecialtyWithDoctorCountDTO> getAllSpecialtiesWithDoctorCount(Pageable pageable);

}
