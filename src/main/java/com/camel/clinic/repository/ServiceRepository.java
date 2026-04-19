package com.camel.clinic.repository;

import com.camel.clinic.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ServiceRepository extends JpaRepository<Service, UUID>, JpaSpecificationExecutor<Service> {
	@Query(value = """
			SELECT s.*
			FROM services s
			WHERE s.deleted_at IS NULL
			  AND to_tsvector('simple', COALESCE(s.name, ''))
				  @@ plainto_tsquery('simple', :keyword)
			ORDER BY ts_rank(
				to_tsvector('simple', COALESCE(s.name, '')),
				plainto_tsquery('simple', :keyword)
			) DESC, s.id
			LIMIT 20
			""", nativeQuery = true)
	List<Service> searchServices(@Param("keyword") String keyword);
}

