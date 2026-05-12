package com.camel.clinic.processor.user;

import com.camel.clinic.dto.user.CreateUserDto;
import com.camel.clinic.entity.Role;
import com.camel.clinic.entity.User;
import com.camel.clinic.service.user.UserServiceImp;
import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Component("userSeedProcessor")
@AllArgsConstructor
@Slf4j
public class UserSeedProcessor implements Processor {

    private final UserServiceImp serviceImp;

    @Override
    public void process(Exchange exchange) throws Exception {
        Faker faker = new Faker(new Locale("vi"));

        List<CreateUserDto> request = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            CreateUserDto dto = new CreateUserDto();

            // Email: dùng UUID để chắc chắn unique
            String uid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
            dto.setEmail("user_" + uid + "@gmail.com");

            // Full name
            String fullName = faker.name().fullName();
            dto.setName(fullName.substring(0, Math.min(fullName.length(), 255)));

            // Phone: prefix hợp lệ + UUID-based suffix để tránh trùng
            String[] prefixes = {"03", "05", "07", "08", "09"};
            String prefix = prefixes[faker.number().numberBetween(0, prefixes.length)];
            // Lấy 8 chữ số từ timestamp + index để đảm bảo unique
            String phoneSuffix = String.format("%08d", (System.nanoTime() + i) % 100_000_000L);
            dto.setPhone(prefix + phoneSuffix);

            // Password
            dto.setPassword("Pass@" + faker.number().digits(4) + "Aa!");

            // Date of birth
            dto.setDateOfBirth(faker.date().birthday(18, 70));

            // Gender
            User.Gender[] genders = User.Gender.values();
            dto.setGender(genders[faker.number().numberBetween(0, genders.length)]);

            // Role
            Role.RoleName[] roles = {
                    Role.RoleName.PATIENT,
                    Role.RoleName.PATIENT,
                    Role.RoleName.PATIENT,
                    Role.RoleName.DOCTOR,
                    Role.RoleName.DOCTOR,
                    Role.RoleName.STAFF
            };
            dto.setRole(roles[faker.number().numberBetween(0, roles.length)]);

            request.add(dto);
        }

        ResponseEntity<?> response = serviceImp.bulkCreate(request);
        exchange.getIn().setBody(response);
    }
}