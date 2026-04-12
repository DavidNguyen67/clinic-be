package com.camel.clinic.repository;

import com.camel.clinic.dto.DoctorDTO;
import com.camel.clinic.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, UUID>, JpaSpecificationExecutor<Doctor> {
    //    get top 10 doctors thì lấy theo averageRating, nếu có nhiều doctor có cùng averageRating
    //    thì lấy theo totalReviews, nếu vẫn còn tie thì lấy theo experienceYears
    @Query("SELECT new com.camel.clinic.dto.DoctorDTO(" +
            "d.id, d.degree, d.experienceYears, d.totalReviews, d.education, d.averageRating, d.user.fullName, d.user.pathAvatar)" +
            " FROM Doctor d" +
            " WHERE d.deletedAt IS NULL" +
            " ORDER BY d.averageRating DESC, d.totalReviews DESC, d.experienceYears DESC" +
            " LIMIT 10")
    List<DoctorDTO> getTopDoctors();
}
