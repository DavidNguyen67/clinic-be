package com.camel.clinic.service.services;

import com.camel.clinic.dto.services.CreateServiceDto;
import com.camel.clinic.dto.services.UpdateServiceDto;
import com.camel.clinic.entity.ClinicService;
import com.camel.clinic.entity.Specialty;
import com.camel.clinic.service.specialty.SpecialtyServiceInv;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class ServicesServiceImp implements ServicesService {
    private final ServicesServiceInv serviceInv;
    private final SpecialtyServiceInv specialtyServiceInv;

    @Override
    public ResponseEntity<?> count() {
        return serviceInv.count();
    }

    @Override
    public ResponseEntity<?> retrieve(String id) {
        return serviceInv.retrieve(id, null);
    }

    @Override
    public ResponseEntity<?> create(CreateServiceDto requestBody) {
        ClinicService clinicService = new ClinicService();
        clinicService.setName(requestBody.getName());
        clinicService.setSlug(requestBody.getSlug());
        clinicService.setDescription(requestBody.getDescription());
        clinicService.setImage(requestBody.getImage());
        clinicService.setPrice(requestBody.getPrice());
        clinicService.setPromotionalPrice(requestBody.getPromotionalPrice());
        clinicService.setDuration(requestBody.getDuration());
        clinicService.setIsActive(requestBody.getIsActive());
        clinicService.setIsFeatured(requestBody.getIsFeatured());

        String specialtyId = requestBody.getSpecialtyId();
        if (specialtyId != null && !specialtyId.isEmpty()) {
            Specialty specialty = specialtyServiceInv.retrieve(specialtyId, null).getBody() instanceof Specialty sp ? sp : null;
            if (specialty == null) {
                throw new IllegalArgumentException("Specialty with ID " + specialtyId + " not found");
            }
            clinicService.setSpecialty(specialty);
        }

        return serviceInv.create(clinicService);

    }

    @Override
    public ResponseEntity<?> update(String id, UpdateServiceDto requestBody) {
        ClinicService clinicService = new ClinicService();
        clinicService.setName(requestBody.getName());
        clinicService.setSlug(requestBody.getSlug());
        clinicService.setDescription(requestBody.getDescription());
        clinicService.setImage(requestBody.getImage());
        clinicService.setPrice(requestBody.getPrice());
        clinicService.setPromotionalPrice(requestBody.getPromotionalPrice());
        clinicService.setDuration(requestBody.getDuration());
        clinicService.setIsActive(requestBody.getIsActive());
        clinicService.setIsFeatured(requestBody.getIsFeatured());

        String specialtyId = requestBody.getSpecialtyId();
        if (specialtyId != null && !specialtyId.isEmpty()) {
            Specialty specialty = specialtyServiceInv.retrieve(specialtyId, null).getBody() instanceof Specialty sp ? sp : null;
            if (specialty == null) {
                throw new IllegalArgumentException("Specialty with ID " + specialtyId + " not found");
            }
            clinicService.setSpecialty(specialty);
        }
        return serviceInv.update(id, clinicService, null);
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
