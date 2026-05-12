package com.camel.clinic.processor.services;

import com.camel.clinic.dto.services.CreateServiceDto;
import com.camel.clinic.entity.Specialty;
import com.camel.clinic.repository.SpecialtyRepository;
import com.camel.clinic.service.services.ServicesServiceImp;
import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component("servicesSeedProcessor")
@AllArgsConstructor
@Slf4j
public class ServicesSeedProcessor implements Processor {
    private final ServicesServiceImp serviceImp;
    private final SpecialtyRepository specialtyRepository;

    @Override
    public void process(Exchange exchange) throws Exception {
        Faker faker = new Faker();

        // Lấy list specialtyIds từ repository
        List<UUID> specialtyIds = specialtyRepository.findAll()
                .stream()
                .map(Specialty::getId)
                .toList();

        List<CreateServiceDto> request = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            CreateServiceDto dto = new CreateServiceDto();

            // Name
            String name = faker.medical().symptoms();
            name = name.substring(0, Math.min(name.length(), 100));
            dto.setName(name);

            // Slug
            String slug = name.toLowerCase()
                    .replaceAll("[^a-z0-9\\s]", "")
                    .trim()
                    .replaceAll("\\s+", "-");
            dto.setSlug(slug.substring(0, Math.min(slug.length(), 120)) + "-" + faker.number().digits(5));

            // Description
            dto.setDescription(faker.lorem().sentence());

            // Image (phải là URL hợp lệ theo @Pattern)
            dto.setImage("https://picsum.photos/seed/" + faker.number().digits(5) + "/400/300");

            // Price: random từ 50,000 đến 5,000,000
            BigDecimal price = BigDecimal.valueOf(faker.number().numberBetween(50, 5000) * 1000L);
            dto.setPrice(price);

            // Promotional price (50% chance có, và phải < price)
            if (faker.bool().bool()) {
                BigDecimal promoPrice = price.multiply(BigDecimal.valueOf(faker.number().randomDouble(2, 50, 90) / 100));
                dto.setPromotionalPrice(promoPrice.setScale(0, RoundingMode.HALF_UP));
            }

            // Duration: random trong khoảng hợp lệ [5, 480]
            dto.setDuration(faker.number().numberBetween(5, 48) * 10);

            dto.setIsActive(faker.bool().bool());
            dto.setIsFeatured(faker.bool().bool());

            // Random specialtyId từ list
            UUID randomSpecialtyId = specialtyIds.get(faker.number().numberBetween(0, specialtyIds.size()));
            dto.setSpecialtyId(randomSpecialtyId.toString());

            request.add(dto);
        }

        ResponseEntity<?> response = serviceImp.bulkCreate(request);
        exchange.getIn().setBody(response);
    }
}
