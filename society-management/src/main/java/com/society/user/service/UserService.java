package com.society.user.service;

import com.society.user.entity.UserEntity;
import java.util.Optional;

public interface UserService {
    Optional<UserEntity> authenticate(String username, String password);
    UserEntity createUser(String username, String password, String role, String firstName, String lastName, String email);
    boolean hasAdminUser();
}
