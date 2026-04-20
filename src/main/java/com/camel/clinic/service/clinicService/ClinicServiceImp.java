package com.camel.clinic.service.clinicService;

import com.camel.clinic.dto.clinicservice.ClinicServiceUpsertRequestDTO;
import com.camel.clinic.entity.ClinicService;
import com.camel.clinic.entity.Specialty;
import com.camel.clinic.repository.ClinicServiceRepository;
import com.camel.clinic.repository.SpecialtyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
public class ClinicServiceImp implements ClinicServiceInterface {
    private final ClinicServiceInv clinicServiceInv;
    private final SpecialtyRepository specialtyRepository;
    private final ObjectMapper objectMapper;
    private final ClinicServiceRepository clinicServiceRepository;

    @Override
    public ResponseEntity<?> list(Map<String, Object> queryParams) {
        return clinicServiceInv.list(queryParams);
    }

    @Override
    public ResponseEntity<?> getById(String id) {
        return clinicServiceInv.retrieve(id, null);
    }

    @Override
    public ResponseEntity<?> create(ClinicServiceUpsertRequestDTO requestDTO) {
        Specialty specialty = specialtyRepository.findById(requestDTO.getSpecialtyId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Specialty not found with id: " + requestDTO.getSpecialtyId()
                ));

        ClinicService clinicService = objectMapper.convertValue(requestDTO, ClinicService.class);
        clinicService.setSpecialty(specialty);
        return clinicServiceInv.create(clinicService);
    }

    @Override
    public ResponseEntity<?> update(String id, ClinicServiceUpsertRequestDTO requestDTO) {
        ClinicService clinicService = objectMapper.convertValue(requestDTO, ClinicService.class);
        return clinicServiceInv.update(id, clinicService, null);
    }

    @Override
    public ResponseEntity<?> delete(String id) {
        return clinicServiceInv.delete(id);
    }
}
