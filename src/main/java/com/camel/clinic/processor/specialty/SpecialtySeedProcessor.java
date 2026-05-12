package com.camel.clinic.processor.specialty;

import com.camel.clinic.dto.specialty.CreateSpecialtyDto;
import com.camel.clinic.entity.Specialty;
import com.camel.clinic.service.specialty.SpecialtyServiceImp;
import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("specialtySeedProcessor")
@AllArgsConstructor
@Slf4j
public class SpecialtySeedProcessor implements Processor {
    private final SpecialtyServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        Faker faker = new Faker();
        int count = 10;

        List<CreateSpecialtyDto> request = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            CreateSpecialtyDto dto = new CreateSpecialtyDto();

            String name = faker.medical().symptoms(); // or faker.lorem().word()
            dto.setName(name.substring(0, Math.min(name.length(), 100)));

            String slug = name.toLowerCase()
                    .replaceAll("[^a-z0-9\\s-]", "")
                    .trim()
                    .replaceAll("\\s+", "-");
            dto.setSlug(slug.substring(0, Math.min(slug.length(), 120)));

            dto.setDescription(faker.lorem().sentence());
            dto.setImage("https://picsum.photos/seed/" + faker.number().digits(5) + "/400/300");
            dto.setDisplayOrder(i);
            dto.setIsActive(faker.bool().bool());
            dto.setSpecialtyType(
                    Specialty.SpecialtyType.values()[
                            faker.number().numberBetween(0, Specialty.SpecialtyType.values().length)
                            ]
            );

            request.add(dto);
        }

        ResponseEntity<?> response = serviceImp.bulkCreate(request);
        exchange.getIn().setBody(response);
    }
}
