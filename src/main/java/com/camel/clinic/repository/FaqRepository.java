package com.camel.clinic.repository;

import com.camel.clinic.entity.Faq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface FaqRepository extends JpaRepository<Faq, UUID>, JpaSpecificationExecutor<Faq> {
}