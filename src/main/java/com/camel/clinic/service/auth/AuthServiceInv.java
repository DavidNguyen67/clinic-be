package com.camel.clinic.service.auth;

import com.camel.clinic.entity.User;
import com.camel.clinic.repository.UserRepository;
import com.camel.clinic.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class AuthServiceInv extends BaseService<User, UserRepository> {
    public AuthServiceInv(UserRepository repository) {
        super(User::new, repository);
    }

    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    public Optional<User> findById(UUID id) {
        return repository.findById(id);
    }

    public User save(User user) {
        return repository.save(user);
    }

    public Optional<User> findByPhone(String phone) {
        return repository.findByPhone(phone);
    }

    public List<String> findEmailsBatch(Pageable pageable) {
        return repository.findEmailsBatch(pageable);
    }
}