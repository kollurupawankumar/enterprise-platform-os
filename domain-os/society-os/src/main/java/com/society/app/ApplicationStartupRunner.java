package com.society.app;

import com.society.infrastructure.health.DatabaseHealthService;
import com.society.user.repository.UserRepository;
import com.society.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartupRunner implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationStartupRunner.class);

    private final DatabaseHealthService databaseHealthService;
    private final UserService userService;
    private final UserRepository userRepository;

    public ApplicationStartupRunner(
            DatabaseHealthService databaseHealthService,
            UserService userService,
            UserRepository userRepository) {
        this.databaseHealthService = databaseHealthService;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        LOGGER.info("======================================");
        LOGGER.info("Society Office OS");
        LOGGER.info("Starting application...");

        if (databaseHealthService.isDatabaseAvailable()) {
            LOGGER.info("Database Status : CONNECTED");
            seedDefaultRoleUsers();
        } else {
            LOGGER.error("Database Status : FAILED");
        }

        LOGGER.info("======================================");
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
            LOGGER.info("Seeded default user account: {} [Role: {}]", username, role);
        }
    }
}