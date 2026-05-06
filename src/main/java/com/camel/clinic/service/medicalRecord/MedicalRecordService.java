package com.camel.clinic.service.medicalRecord;

import com.camel.clinic.dto.medicalRecord.CreateMedicalRecordDto;
import com.camel.clinic.dto.medicalRecord.UpdateMedicalRecordDto;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface MedicalRecordService {
    ResponseEntity<?> list(Map<String, Object> queryParams);

    ResponseEntity<?> count();

    ResponseEntity<?> retrieve(String id);

    ResponseEntity<?> create(CreateMedicalRecordDto requestBody);

    ResponseEntity<?> update(String id, UpdateMedicalRecordDto requestBody);

    ResponseEntity<?> delete(String id);

    ResponseEntity<?> restore(String id);
}
