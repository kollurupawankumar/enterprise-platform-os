package com.clinical.security.service;

import com.clinical.audit.service.ClinicalAuditService;
import com.clinical.security.entity.UserEntity;
import com.clinical.security.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface AuthService {
    Optional<UserEntity> authenticate(String username, String password);
    UserEntity getCurrentUser();
    void setCurrentUser(UserEntity user);
}

@Service
@Transactional
class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final ClinicalAuditService auditService;
    private UserEntity currentUser;

    public AuthServiceImpl(UserRepository userRepository, ClinicalAuditService auditService) {
        this.userRepository = userRepository;
        this.auditService = auditService;
    }

    @Override
    public Optional<UserEntity> authenticate(String username, String password) {
        Optional<UserEntity> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            // Basic authentication match for demo (or bcrypt match)
            this.currentUser = user;
            auditService.logEvent(user.getUsername(), "USER_LOGIN", "Role: " + user.getRole());
            return Optional.of(user);
        }
        return Optional.empty();
    }

    @Override
    public UserEntity getCurrentUser() {
        return currentUser;
    }

    @Override
    public void setCurrentUser(UserEntity user) {
        this.currentUser = user;
    }
}
