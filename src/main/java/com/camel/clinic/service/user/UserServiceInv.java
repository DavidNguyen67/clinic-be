package com.camel.clinic.service.user;

import com.camel.clinic.entity.Role;
import com.camel.clinic.entity.User;
import com.camel.clinic.repository.UserRepository;
import com.camel.clinic.service.BaseService;
import com.camel.clinic.service.CommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class UserServiceInv extends BaseService<User, UserRepository> {

    public UserServiceInv(UserRepository repository) {
        super(User::new, repository);
    }

    @Override
    protected Specification<User> buildSpec(Map<String, Object> queryParams) {
        return Specification.<User>unrestricted()
                .and(notDeleted())
                .and(fieldEquals("gender", CommonService.parseToEnum(User.Gender.class, queryParams.get("gender"))))
                .and(fieldEquals("status", CommonService.parseToEnum(User.UserStatus.class, queryParams.get("status"))))
                .and(multiFieldIn(
                        parseEnumList(queryParams.get("role"), Role.RoleName.class),
                        new String[]{"role"}
                ))
                .and(fieldLike("fullName", (String) queryParams.get("fullName")));
    }

    public long countByRole(Role.RoleName role, Map<String, Object> baseParams) {
        Map<String, Object> params = new HashMap<>(baseParams);
        params.put("role", role != null ? role.name() : null);
        return repository.count(buildSpec(params));
    }
}