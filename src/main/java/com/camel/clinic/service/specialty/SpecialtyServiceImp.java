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
    public ResponseEntity<?> count() {
        return specialtyServiceInv.count();
    }

    @Override
    public ResponseEntity<?> retrieve(String id) {
        return specialtyServiceInv.retrieve(id, null);
    }

    @Override
    public ResponseEntity<?> create(CreateSpecialtyDto requestBody) {
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
    public ResponseEntity<?> update(String id, UpdateSpecialtyDto requestBody) {
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
    public ResponseEntity<?> delete(String id) {
        return specialtyServiceInv.delete(id);
    }

    @Override
    public ResponseEntity<?> restore(String id) {
        return specialtyServiceInv.restore(id);
    }

    public ResponseEntity<?> list(Map<String, Object> queryParams) {
        return specialtyServiceInv.list(queryParams);
    }
}
