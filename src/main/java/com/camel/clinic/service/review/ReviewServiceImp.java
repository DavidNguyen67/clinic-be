package com.camel.clinic.service.review;

import com.camel.clinic.dto.review.CreateReviewDto;
import com.camel.clinic.dto.review.UpdateReviewDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class ReviewServiceImp implements ReviewService {
    private final ReviewServiceInv serviceInv;

    @Override
    public ResponseEntity<?> list(Map<String, Object> queryParams) {
        return serviceInv.list(queryParams);
    }

    @Override
    public ResponseEntity<?> count() {
        return serviceInv.count();
    }

    @Override
    public ResponseEntity<?> retrieve(String id) {
        return serviceInv.retrieve(id, null);
    }

    @Override
    public ResponseEntity<?> retrieveByAppointmentId(String appointmentId) {
        return serviceInv.retrieveByAppointmentId(appointmentId);
    }

    @Override
    public ResponseEntity<?> create(CreateReviewDto requestBody) {
        return null;
    }

    @Override
    public ResponseEntity<?> update(String id, UpdateReviewDto requestBody) {
        return null;
    }

    @Override
    public ResponseEntity<?> delete(String id) {
        return serviceInv.delete(id);
    }

    @Override
    public ResponseEntity<?> restore(String id) {
        return serviceInv.restore(id);
    }
}
