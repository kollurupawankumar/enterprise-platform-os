package com.society.user.service;

import com.society.user.entity.UserEntity;
import com.society.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void testCreateUserHashesPassword() {
        UserEntity dummyUser = new UserEntity();
        dummyUser.setUsername("admin");
        dummyUser.setPasswordHash("hashed_password");
        
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserEntity created = userService.createUser("admin", "plain_password", "ADMINISTRATOR", "First", "Last", "admin@society.org");

        assertNotNull(created);
        assertEquals("admin", created.getUsername());
        assertNotEquals("plain_password", created.getPasswordHash());
        assertTrue(created.getPasswordHash().startsWith("$2a$")); // BCrypt prefix
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void testAuthenticateSuccess() {
        UserEntity dummyUser = new UserEntity();
        dummyUser.setUsername("secretary");
        dummyUser.setPasswordHash(org.mindrot.jbcrypt.BCrypt.hashpw("secret123", org.mindrot.jbcrypt.BCrypt.gensalt()));
        dummyUser.setActive(true);

        when(userRepository.findByUsername("secretary")).thenReturn(Optional.of(dummyUser));

        Optional<UserEntity> auth = userService.authenticate("secretary", "secret123");

        assertTrue(auth.isPresent());
        assertEquals("secretary", auth.get().getUsername());
    }

    @Test
    void testAuthenticateFailure() {
        UserEntity dummyUser = new UserEntity();
        dummyUser.setUsername("secretary");
        dummyUser.setPasswordHash("$2a$10$tM.r7iY/a1WbZpIqUu27fO5vE44n239L3lJp5Jm2GvXGpewA2B1vW");
        dummyUser.setActive(true);

        when(userRepository.findByUsername("secretary")).thenReturn(Optional.of(dummyUser));

        Optional<UserEntity> auth = userService.authenticate("secretary", "wrong_password");

        assertFalse(auth.isPresent());
    }
}
