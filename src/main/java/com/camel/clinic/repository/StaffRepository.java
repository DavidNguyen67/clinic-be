package com.camel.clinic.repository;

import com.camel.clinic.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StaffRepository extends JpaRepository<Staff, UUID>, JpaSpecificationExecutor<Staff> {
    @Query("SELECT s FROM Staff s WHERE s.user.id = :userId")
    Optional<Staff> findByUserId(UUID userId);

    boolean existsByStaffCode(String staffCode);
}

