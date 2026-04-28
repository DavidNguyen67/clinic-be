package com.camel.clinic.repository;

import com.camel.clinic.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID>, JpaSpecificationExecutor<Review> {

    @Query("""
            SELECT r FROM Review r
            WHERE r.doctor.id = :doctorId
              AND r.deletedAt IS NULL
              AND r.status = com.camel.clinic.entity.Review.ReviewStatus.APPROVED
            ORDER BY r.createdAt DESC
            """)
    List<Review> findApprovedByDoctorId(@Param("doctorId") UUID doctorId);
}

