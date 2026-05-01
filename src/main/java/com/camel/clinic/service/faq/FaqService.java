package com.camel.clinic.service.faq;

import com.camel.clinic.dto.faq.CreateFaqDto;
import com.camel.clinic.dto.faq.UpdateFaqDto;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface FaqService {
    ResponseEntity<?> list(Map<String, Object> queryParams);

    ResponseEntity<?> count();

    ResponseEntity<?> retrieve(String id);

    ResponseEntity<?> create(CreateFaqDto requestBody);

    ResponseEntity<?> update(String id, UpdateFaqDto requestBody);

    ResponseEntity<?> delete(String id);

    ResponseEntity<?> restore(String id);
}
