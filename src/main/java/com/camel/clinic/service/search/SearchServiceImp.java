package com.camel.clinic.service.search;

import com.camel.clinic.entity.ClinicService;
import com.camel.clinic.entity.Doctor;
import com.camel.clinic.entity.Specialty;
import com.camel.clinic.exception.BadRequestException;
import com.camel.clinic.repository.DoctorRepository;
import com.camel.clinic.repository.ServiceRepository;
import com.camel.clinic.repository.SpecialtyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class SearchServiceImp {

    private final DoctorRepository doctorRepository;
    private final SpecialtyRepository specialtyRepository;
    private final ServiceRepository serviceRepository;

    public ResponseEntity<?> search(String query, String type) {
        String keyword = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        if (keyword.isBlank()) {
            throw new BadRequestException("q is required");
        }

        String normalizedType = type == null ? "" : type.trim().toLowerCase(Locale.ROOT);
        if (!normalizedType.isBlank()
                && !"doctor".equals(normalizedType)
                && !"specialty".equals(normalizedType)
                && !"service".equals(normalizedType)) {
            throw new BadRequestException("type must be doctor|specialty|service");
        }

        Map<String, Object> result = new LinkedHashMap<>();

        if (normalizedType.isBlank() || "doctor".equals(normalizedType)) {
            List<Doctor> doctors = doctorRepository.searchDoctors(keyword);
            result.put("doctors", doctors);
        }

        if (normalizedType.isBlank() || "specialty".equals(normalizedType)) {
            List<Specialty> specialties = specialtyRepository.searchSpecialties(keyword);
            result.put("specialties", specialties);
        }

        if (normalizedType.isBlank() || "service".equals(normalizedType)) {
            List<ClinicService> clinicServices = serviceRepository.searchServices(keyword);
            result.put("services", clinicServices);
        }

        return ResponseEntity.ok(result);
    }
}

