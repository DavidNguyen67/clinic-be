package com.camel.clinic.service.user;

import com.camel.clinic.entity.Role;
import com.camel.clinic.entity.User;
import com.camel.clinic.repository.UserRepository;
import com.camel.clinic.service.BaseService;
import com.camel.clinic.service.CommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class UserServiceInv extends BaseService<User, UserRepository> {
    private final CommonService commonService;

    public UserServiceInv(UserRepository repository, CommonService commonService) {
        super(User::new, repository);
        this.commonService = commonService;
    }

    @Override
    protected Specification<User> buildSpec(Map<String, Object> queryParams) {
        return Specification
                .where(notDeleted())
                .and(hasField("gender", commonService.parseEnum(User.Gender.class, queryParams.get("gender"))))
                .and(hasField("status", commonService.parseEnum(User.UserStatus.class, queryParams.get("status"))))
                .and(hasField("role", commonService.parseEnum(Role.RoleName.class, queryParams.get("role"))))
                .and(fieldLike("fullName", (String) queryParams.get("keyword")));
    }
}