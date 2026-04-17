package com.camel.clinic.repository;

import com.camel.clinic.dto.DoctorDTO;
import com.camel.clinic.entity.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, UUID>, JpaSpecificationExecutor<Doctor> {
    //    get top 10 doctors thì lấy theo averageRating, nếu có nhiều doctor có cùng averageRating
    //    thì lấy theo totalReviews, nếu vẫn còn tie thì lấy theo experienceYears
    @Query("SELECT new com.camel.clinic.dto.DoctorDTO(" +
            "d.id, d.degree, d.experienceYears, d.totalReviews, d.education, d.averageRating, d.user.fullName, d.user.pathAvatar,d.consultationFee,d.bio,d.specialty)" +
            " FROM Doctor d" +
            " WHERE d.deletedAt IS NULL" +
            " ORDER BY d.averageRating DESC, d.totalReviews DESC, d.experienceYears DESC" +
            " LIMIT 10")
    List<DoctorDTO> getTopDoctors();

    @Query(value = "SELECT new com.camel.clinic.dto.DoctorDTO(" +
            "d.id, d.degree, d.experienceYears, d.totalReviews, d.education, d.averageRating, u.fullName, u.pathAvatar,d.consultationFee,d.bio,d.specialty)" +
            " FROM Doctor d JOIN d.user u JOIN d.specialty s" +
            " WHERE d.deletedAt IS NULL" +
            " AND (COALESCE(:doctorName, '') = '' OR cast(function('unaccent', LOWER(u.fullName)) as string) LIKE cast(function('unaccent', LOWER(CONCAT('%', :doctorName, '%'))) as string))" +
            " AND (COALESCE(:specialtyName, '') = '' OR cast(function('unaccent', LOWER(s.name)) as string) LIKE cast(function('unaccent', LOWER(CONCAT('%', :specialtyName, '%'))) as string))" +
            " AND (:specialtyId IS NULL OR s.id = :specialtyId)" +
            " ORDER BY d.averageRating DESC, d.totalReviews DESC, d.experienceYears DESC",
            countQuery = "SELECT COUNT(d) FROM Doctor d JOIN d.user u JOIN d.specialty s" +
                    " WHERE d.deletedAt IS NULL" +
                    " AND (COALESCE(:doctorName, '') = '' OR cast(function('unaccent', LOWER(u.fullName)) as string) LIKE cast(function('unaccent', LOWER(CONCAT('%', :doctorName, '%'))) as string))" +
                    " AND (COALESCE(:specialtyName, '') = '' OR cast(function('unaccent', LOWER(s.name)) as string) LIKE cast(function('unaccent', LOWER(CONCAT('%', :specialtyName, '%'))) as string))" +
                    " AND (:specialtyId IS NULL OR s.id = :specialtyId)")
    Page<DoctorDTO> filterDoctors(
            @Param("doctorName") String doctorName,
            @Param("specialtyName") String specialtyName,
            @Param("specialtyId") UUID specialtyId,
            Pageable pageable
    );

    @Query("""
            SELECT d
            FROM Doctor d
            LEFT JOIN Specialty s ON d MEMBER OF s.doctors
            LEFT JOIN DoctorSchedule ds ON ds.doctor.id = d.id AND ds.deletedAt IS NULL
            WHERE d.user.id = :userId
            AND d.deletedAt IS NULL""")
    Optional<Doctor> findByUserId(UUID userId);
}
