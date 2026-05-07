package com.camel.clinic.service.promotion;

import com.camel.clinic.dto.promotion.CreatePromotionDto;
import com.camel.clinic.dto.promotion.UpdatePromotionDto;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface PromotionService {
    ResponseEntity<?> list(Map<String, Object> queryParams);

    ResponseEntity<?> count();

    ResponseEntity<?> retrieve(String id);

    ResponseEntity<?> create(CreatePromotionDto requestBody);

    ResponseEntity<?> update(String id, UpdatePromotionDto requestBody);

    ResponseEntity<?> delete(String id);

    ResponseEntity<?> restore(String id);
}
