package com.camel.clinic.processor.doctorProfile;

import com.camel.clinic.dto.doctorProfile.CreateDoctorProfileDto;
import com.camel.clinic.entity.Role;
import com.camel.clinic.repository.SpecialtyRepository;
import com.camel.clinic.repository.UserRepository;
import com.camel.clinic.service.doctorProfile.DoctorProfileServiceImp;
import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component("doctorProfileSeedProcessor")
@AllArgsConstructor
@Slf4j
public class DoctorProfileSeedProcessor implements Processor {

    private final DoctorProfileServiceImp serviceImp;
    private final SpecialtyRepository specialtyRepository;
    private final UserRepository userRepository;

    @Override
    public void process(Exchange exchange) throws Exception {
        Faker faker = new Faker();

        List<String> specialtyIds = specialtyRepository.findAll()
                .stream()
                .map(s -> s.getId().toString())
                .toList();

        List<String> userIds = userRepository.findAllByRole(Role.RoleName.DOCTOR)
                .stream()
                .map(u -> u.getId().toString())
                .toList();

        if (specialtyIds.isEmpty()) {
            throw new IllegalStateException("No specialties found. Please seed specialties first.");
        }
        if (userIds.isEmpty()) {
            throw new IllegalStateException("No users found. Please seed users first.");
        }

        List<CreateDoctorProfileDto> request = new ArrayList<>();

        for (int i = 0; i < userIds.size(); i++) {
            CreateDoctorProfileDto dto = new CreateDoctorProfileDto();

            // ✅ Mỗi userId chỉ dùng 1 lần, không random
            dto.setUserId(userIds.get(i));

            // Random specialtyId vẫn OK vì không có unique constraint
            String randomSpecialtyId = specialtyIds.get(faker.number().numberBetween(0, specialtyIds.size()));
            dto.setSpecialtyId(randomSpecialtyId);

            String[] degrees = {"MD", "PhD", "MBBS", "DO", "MS", "DDS", "MPH"};
            dto.setDegree(degrees[faker.number().numberBetween(0, degrees.length)]);

            dto.setExperienceYears(faker.number().numberBetween(0, 40));

            String education = faker.university().name() + " - " + faker.number().numberBetween(1990, 2020);
            dto.setEducation(education.substring(0, Math.min(education.length(), 2000)));

            String bio = faker.lorem().paragraph(2);
            dto.setBio(bio.substring(0, Math.min(bio.length(), 2000)));

            BigDecimal fee = BigDecimal.valueOf(faker.number().numberBetween(0, 40) * 50_000L);
            dto.setConsultationFee(fee);

            dto.setIsFeatured(faker.bool().bool());

            request.add(dto);
        }
        ResponseEntity<?> response = serviceImp.bulkCreate(request);
        exchange.getIn().setBody(response);
    }
}