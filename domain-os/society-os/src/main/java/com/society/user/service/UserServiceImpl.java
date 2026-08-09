package com.society.user.service;

import com.society.user.entity.UserEntity;
import com.society.user.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserEntity> authenticate(String username, String password) {
        Optional<UserEntity> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent() && userOpt.get().isActive()) {
            UserEntity user = userOpt.get();
            if (BCrypt.checkpw(password, user.getPasswordHash())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public UserEntity createUser(String username, String password, String role, String firstName, String lastName, String email) {
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPasswordHash(BCrypt.hashpw(password, BCrypt.gensalt()));
        user.setRole(role);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setActive(true);
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasAdminUser() {
        return userRepository.existsByRole("ADMINISTRATOR");
    }
}
