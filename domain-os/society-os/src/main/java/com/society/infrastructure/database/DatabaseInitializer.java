package com.society.infrastructure.database;

import com.society.infrastructure.filesystem.ApplicationDirectories;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;

@Component
public class DatabaseInitializer {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(DatabaseInitializer.class);

    private final ApplicationDirectories directories;

    public DatabaseInitializer(ApplicationDirectories directories) {
        this.directories = directories;
    }

    @PostConstruct
    public void initialize() throws IOException {

        Files.createDirectories(directories.getDatabaseDirectory());
        Files.createDirectories(directories.getBackupDirectory());
        Files.createDirectories(directories.getLogDirectory());
        Files.createDirectories(directories.getReportsDirectory());

        LOGGER.info("Application home : {}", directories.getApplicationHome());
        LOGGER.info("Database         : {}", directories.getDatabaseFile());
        LOGGER.info("Backup directory : {}", directories.getBackupDirectory());
        LOGGER.info("Reports directory: {}", directories.getReportsDirectory());
        LOGGER.info("Log directory    : {}", directories.getLogDirectory());
    }
}