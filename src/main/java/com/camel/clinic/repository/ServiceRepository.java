package com.camel.clinic.repository;

import com.camel.clinic.entity.ClinicService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ServiceRepository extends JpaRepository<ClinicService, UUID>, JpaSpecificationExecutor<ClinicService> {
    @Query(value = """
            SELECT s.*
            FROM clinicServices s
            WHERE s.deleted_at IS NULL
              AND to_tsvector('simple', COALESCE(s.name, ''))
            	  @@ plainto_tsquery('simple', :keyword)
            ORDER BY ts_rank(
            	to_tsvector('simple', COALESCE(s.name, '')),
            	plainto_tsquery('simple', :keyword)
            ) DESC, s.id
            LIMIT 20
            """, nativeQuery = true)
    List<ClinicService> searchServices(@Param("keyword") String keyword);

    boolean existsBySlugIgnoreCase(String slug);

    boolean existsBySlugIgnoreCaseAndIdNot(String slug, UUID id);
}

