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
    private final SpecialtyServiceInv serviceInv;

    @Override
    public ResponseEntity<?> count() {
        return serviceInv.count();
    }

    @Override
    public ResponseEntity<?> retrieve(String id) {
        return serviceInv.retrieve(id, null);
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

        return serviceInv.create(specialty);
    }

    @Override
    public ResponseEntity<?> update(String id, UpdateSpecialtyDto requestBody) {
        Specialty specialty = serviceInv.retrieve(id, null).getBody() instanceof Specialty s ? s : null;
        if (specialty == null) {
            throw new IllegalArgumentException("Specialty with ID " + id + " not found");
        }
        specialty.setName(requestBody.getName());
        specialty.setSlug(requestBody.getSlug());
        specialty.setDescription(requestBody.getDescription());
        specialty.setImage(requestBody.getImage());
        specialty.setDisplayOrder(requestBody.getDisplayOrder());
        specialty.setIsActive(requestBody.getIsActive());
        specialty.setSpecialtyType(requestBody.getSpecialtyType());

        return serviceInv.update(id, specialty, null);
    }

    @Override
    public ResponseEntity<?> delete(String id) {
        return serviceInv.delete(id);
    }

    @Override
    public ResponseEntity<?> restore(String id) {
        return serviceInv.restore(id);
    }

    @Override
    public ResponseEntity<?> list(Map<String, Object> queryParams) {
        return serviceInv.list(queryParams);
    }
}
