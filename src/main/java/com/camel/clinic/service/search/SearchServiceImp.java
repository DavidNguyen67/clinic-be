package com.camel.clinic.service.search;

import com.camel.clinic.entity.Doctor;
import com.camel.clinic.entity.Specialty;
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
import java.util.stream.Collectors;

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
            return ResponseEntity.badRequest().body(Map.of("error", "q is required"));
        }

        Map<String, Object> result = new LinkedHashMap<>();

        if (type == null || type.isBlank() || "doctor".equalsIgnoreCase(type)) {
            List<Doctor> doctors = doctorRepository.findAll().stream()
                    .filter(d -> d.getDeletedAt() == null)
                    .filter(d -> d.getUser() != null && d.getUser().getFullName() != null)
                    .filter(d -> d.getUser().getFullName().toLowerCase(Locale.ROOT).contains(keyword)
                            || (d.getSpecialty() != null && d.getSpecialty().getName() != null
                            && d.getSpecialty().getName().toLowerCase(Locale.ROOT).contains(keyword)))
                    .limit(20)
                    .collect(Collectors.toList());
            result.put("doctors", doctors);
        }

        if (type == null || type.isBlank() || "specialty".equalsIgnoreCase(type)) {
            List<Specialty> specialties = specialtyRepository.findAll().stream()
                    .filter(s -> s.getDeletedAt() == null)
                    .filter(s -> s.getName() != null && s.getName().toLowerCase(Locale.ROOT).contains(keyword))
                    .limit(20)
                    .collect(Collectors.toList());
            result.put("specialties", specialties);
        }

        if (type == null || type.isBlank() || "service".equalsIgnoreCase(type)) {
            List<com.camel.clinic.entity.Service> services = serviceRepository.findAll().stream()
                    .filter(s -> s.getDeletedAt() == null)
                    .filter(s -> s.getName() != null && s.getName().toLowerCase(Locale.ROOT).contains(keyword))
                    .limit(20)
                    .collect(Collectors.toList());
            result.put("services", services);
        }

        return ResponseEntity.ok(result);
    }
}

