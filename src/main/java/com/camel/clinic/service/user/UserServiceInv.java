package com.camel.clinic.service.user;

import com.camel.clinic.entity.User;
import com.camel.clinic.repository.UserRepository;
import com.camel.clinic.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceInv extends BaseService<User, UserRepository> {
    public UserServiceInv(UserRepository repository) {
        super(User::new, repository);
    }
}