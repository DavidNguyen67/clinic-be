package com.camel.clinic.service.user;

import com.camel.clinic.entity.Role;
import com.camel.clinic.entity.User;
import com.camel.clinic.repository.UserRepository;
import com.camel.clinic.service.BaseService;
import com.camel.clinic.service.CommonService;
import com.camel.clinic.service.EmailUniqueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class UserServiceInv extends BaseService<User, UserRepository> {
    private final EmailUniqueService emailUniqueService;

    public UserServiceInv(UserRepository repository, EmailUniqueService emailUniqueService) {
        super(User::new, repository);
        this.emailUniqueService = emailUniqueService;
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
                .and(multiFieldEquals(queryParams.get("email"),
                        new String[]{"email"}
                ))
                .and(fieldLike("fullName", (String) queryParams.get("fullName")));
    }

    public long countByRole(Role.RoleName role, Map<String, Object> baseParams) {
        Map<String, Object> params = new HashMap<>(baseParams);
        params.put("role", role != null ? role.name() : null);
        return repository.count(buildSpec(params));
    }

    public ResponseEntity<?> countWithSpec(Map<String, Object> baseParams) {
        Object raw = baseParams.get("email");
        if (!(raw instanceof String emailParam) || emailParam.isBlank()) {
            return ResponseEntity.ok(0L);
        }

        if (emailUniqueService.existsInCache(emailParam)) {
            return ResponseEntity.ok(1L);
        }

        long count = repository.count(buildSpec(baseParams));

        return ResponseEntity.ok(count);
    }
}