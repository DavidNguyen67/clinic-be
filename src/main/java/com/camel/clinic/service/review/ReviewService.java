package com.camel.clinic.service.review;

import com.camel.clinic.dto.review.CreateReviewDto;
import com.camel.clinic.dto.review.UpdateReviewDto;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface ReviewService {
    ResponseEntity<?> list(Map<String, Object> queryParams);

    ResponseEntity<?> count();

    ResponseEntity<?> retrieve(String id);

    ResponseEntity<?> retrieveByAppointmentId(String appointmentId);

    ResponseEntity<?> create(CreateReviewDto requestBody);

    ResponseEntity<?> update(String id, UpdateReviewDto requestBody);

    ResponseEntity<?> delete(String id);

    ResponseEntity<?> restore(String id);
}
