package com.camel.clinic.service.user;

import com.camel.clinic.dto.user.CreateUserDto;
import com.camel.clinic.dto.user.UserStatisticsDto;
import com.camel.clinic.entity.Role;
import com.camel.clinic.entity.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImp implements UserService {
    private final UserServiceInv serviceInv;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<?> list(Map<String, Object> queryParams) {
        return serviceInv.list(queryParams);
    }

    @Override
    public ResponseEntity<?> bulkCreate(List<CreateUserDto> requestBody) {
        List<User> users = requestBody.stream().map(dto -> {
            User user = new User();
            user.setEmail(dto.getEmail());
            user.setFullName(dto.getName());
            user.setPhone(dto.getPhone());
            user.setDateOfBirth(dto.getDateOfBirth());
            user.setRole(dto.getRole());
            user.setGender(dto.getGender());
            user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
            user.setPathAvatar(dto.getPathAvatar());
            user.setPhoneVerified(false);
            user.setEmailVerified(false);
            return user;
        }).toList();

        return serviceInv.bulkCreate(users);
    }

    @Override
    public ResponseEntity<?> count() {
        return serviceInv.count();
    }

    @Override
    public ResponseEntity<?> calculateStatistics() {
        long patientsCount = serviceInv.countByRole(Role.RoleName.PATIENT, Map.of());
        long doctorsCount = serviceInv.countByRole(Role.RoleName.DOCTOR, Map.of());
        long adminsCount = serviceInv.countByRole(Role.RoleName.ADMIN, Map.of());
        long staffsCount = serviceInv.countByRole(Role.RoleName.STAFF, Map.of());
        UserStatisticsDto statistics = UserStatisticsDto.builder()
                .patientsCount(patientsCount)
                .doctorsCount(doctorsCount)
                .adminsCount(adminsCount)
                .staffsCount(staffsCount)
                .build();
        statistics.calculateTotalCount();

        return ResponseEntity.ok(statistics);
    }

    @Override
    public ResponseEntity<?> countWithSpec(Map<String, Object> queryParams) {
        return serviceInv.countWithSpec(queryParams);
    }

    @Override
    public ResponseEntity<?> retrieve(String id) {
        return serviceInv.retrieve(id, null);
    }

}
