package com.camel.clinic.service.specialty;

import com.camel.clinic.dto.specialty.CreateSpecialtyDto;
import com.camel.clinic.dto.specialty.UpdateSpecialtyDto;
import com.camel.clinic.entity.Specialty;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class SpecialtyServiceImp implements SpecialtyService {
    private final SpecialtyServiceInv specialtyServiceInv;

    @Override
    public ResponseEntity<?> countAllSpecialties() {
        return specialtyServiceInv.count();
    }

    @Override
    public ResponseEntity<?> getSpecialtyById(String id) {
        return specialtyServiceInv.retrieve(id, null);
    }

    @Override
    public ResponseEntity<?> createSpecialty(CreateSpecialtyDto requestBody) {
        Specialty specialty = new Specialty();
        specialty.setName(requestBody.getName());
        specialty.setSlug(requestBody.getSlug());
        specialty.setDescription(requestBody.getDescription());
        specialty.setImage(requestBody.getImage());
        specialty.setDisplayOrder(requestBody.getDisplayOrder());
        specialty.setIsActive(requestBody.getIsActive());
        specialty.setSpecialtyType(requestBody.getSpecialtyType());

        return specialtyServiceInv.create(specialty);
    }

    @Override
    public ResponseEntity<?> updateSpecialty(String id, UpdateSpecialtyDto requestBody) {
        Specialty specialty = new Specialty();
        specialty.setName(requestBody.getName());
        specialty.setSlug(requestBody.getSlug());
        specialty.setDescription(requestBody.getDescription());
        specialty.setImage(requestBody.getImage());
        specialty.setDisplayOrder(requestBody.getDisplayOrder());
        specialty.setIsActive(requestBody.getIsActive());
        specialty.setSpecialtyType(requestBody.getSpecialtyType());

        return specialtyServiceInv.update(id, specialty, null);
    }

    @Override
    public ResponseEntity<?> deleteSpecialty(String id) {
        return specialtyServiceInv.delete(id);
    }

    public ResponseEntity<?> getAllSpecialties(Map<String, Object> queryParams) {
        return specialtyServiceInv.list(queryParams);
    }
}
