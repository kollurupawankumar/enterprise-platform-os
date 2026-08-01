package com.society.app;

import com.society.infrastructure.health.DatabaseHealthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartupRunner implements ApplicationRunner {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(ApplicationStartupRunner.class);

    private final DatabaseHealthService databaseHealthService;

    public ApplicationStartupRunner(DatabaseHealthService databaseHealthService) {
        this.databaseHealthService = databaseHealthService;
    }

    @Override
    public void run(ApplicationArguments args) {

        LOGGER.info("======================================");
        LOGGER.info("Society Management System");
        LOGGER.info("Starting application...");

        if (databaseHealthService.isDatabaseAvailable()) {
            LOGGER.info("Database Status : CONNECTED");
        } else {
            LOGGER.error("Database Status : FAILED");
        }

        LOGGER.info("======================================");
    }
}