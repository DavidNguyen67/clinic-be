package com.camel.clinic.service.services;

import com.camel.clinic.dto.services.CreateServiceDto;
import com.camel.clinic.dto.services.UpdateServiceDto;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface ServicesService {
    ResponseEntity<?> list(Map<String, Object> queryParams);

    ResponseEntity<?> count();

    ResponseEntity<?> retrieve(String id);

    ResponseEntity<?> create(CreateServiceDto requestBody);

    ResponseEntity<?> bulkCreate(List<CreateServiceDto> requestBody);

    ResponseEntity<?> update(String id, UpdateServiceDto requestBody);

    ResponseEntity<?> delete(String id);

    ResponseEntity<?> restore(String id);
}
