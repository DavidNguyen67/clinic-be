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
    private final ServicesServiceInv servicesServiceInv;
    private final SpecialtyServiceInv specialtyServiceInv;

    @Override
    public ResponseEntity<?> count() {
        return servicesServiceInv.count();
    }

    @Override
    public ResponseEntity<?> retrieve(String id) {
        return servicesServiceInv.retrieve(id, null);
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

        Specialty specialty = specialtyServiceInv.retrieve(requestBody.getSpecialtyId(), null).getBody() instanceof Specialty sp ? sp : null;
        if (specialty == null) {
            throw new IllegalArgumentException("Specialty with ID " + requestBody.getSpecialtyId() + " not found");
        } else {
            clinicService.setSpecialty(specialty);
            return servicesServiceInv.create(clinicService);
        }
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
        if (specialtyId == null || specialtyId.isEmpty()) {
            return servicesServiceInv.update(id, clinicService, null);
        } else {
            Specialty specialty = specialtyServiceInv.retrieve(requestBody.getSpecialtyId(), null).getBody() instanceof Specialty sp ? sp : null;
            if (specialty == null) {
                throw new IllegalArgumentException("Specialty with ID " + requestBody.getSpecialtyId() + " not found");
            } else {
                clinicService.setSpecialty(specialty);
                return servicesServiceInv.update(id, clinicService, null);
            }
        }
    }

    @Override
    public ResponseEntity<?> delete(String id) {
        return servicesServiceInv.delete(id);
    }

    @Override
    public ResponseEntity<?> restore(String id) {
        return servicesServiceInv.restore(id);
    }

    public ResponseEntity<?> list(Map<String, Object> queryParams) {
        return servicesServiceInv.list(queryParams);
    }
}
