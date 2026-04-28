package com.camel.clinic.service.services;

import com.camel.clinic.dto.services.CreateServiceDto;
import com.camel.clinic.dto.services.UpdateServiceDto;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface ServicesService {
    ResponseEntity<?> getAllServices(Map<String, Object> queryParams);

    ResponseEntity<?> countAllServices();

    ResponseEntity<?> getServiceById(String id);

    ResponseEntity<?> createService(CreateServiceDto requestBody);

    ResponseEntity<?> updateService(String id, UpdateServiceDto requestBody);

    ResponseEntity<?> deleteService(String id);
}
