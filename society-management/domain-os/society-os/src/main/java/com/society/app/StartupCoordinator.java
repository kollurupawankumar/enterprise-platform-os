package com.society.app;

import com.society.common.navigation.NavigationManager;
import com.society.common.navigation.View;
import com.society.society.service.SocietyService;
import com.society.user.repository.UserRepository;
import com.society.user.service.UserService;
import org.springframework.stereotype.Component;

@Component
public class StartupCoordinator {

    private final NavigationManager navigationManager;
    private final SocietyService societyService;
    private final UserService userService;
    private final UserRepository userRepository;

    public StartupCoordinator(
            NavigationManager navigationManager,
            SocietyService societyService,
            UserService userService,
            UserRepository userRepository) {

        this.navigationManager = navigationManager;
        this.societyService = societyService;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    public void start() {
        seedDefaultRoleUsers();

        if (societyService.exists() && userService.hasAdminUser()) {
            navigationManager.navigate(View.LOGIN);
        } else {
            navigationManager.navigate(View.SOCIETY_SETUP);
        }
    }

    private void seedDefaultRoleUsers() {
        createDefaultUserIfAbsent("admin", "admin123", "ADMINISTRATOR", "System", "Administrator", "admin@society.org");
        createDefaultUserIfAbsent("president", "admin123", "PRESIDENT", "Rajesh", "Sharma", "president@society.org");
        createDefaultUserIfAbsent("secretary", "admin123", "SECRETARY", "Amit", "Verma", "secretary@society.org");
        createDefaultUserIfAbsent("treasurer", "admin123", "TREASURER", "Suresh", "Patel", "treasurer@society.org");
        createDefaultUserIfAbsent("auditor", "admin123", "AUDITOR", "Vikram", "Mehta", "auditor@society.org");
        createDefaultUserIfAbsent("assistant", "admin123", "OFFICE_ASSISTANT", "Pooja", "Nair", "assistant@society.org");
    }

    private void createDefaultUserIfAbsent(String username, String password, String role, String firstName, String lastName, String email) {
        if (!userRepository.existsByUsername(username)) {
            userService.createUser(username, password, role, firstName, lastName, email);
        }
    }
}