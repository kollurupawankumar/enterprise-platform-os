package com.society.infrastructure.filesystem;

import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class DefaultApplicationDirectories implements ApplicationDirectories {

    private static final Path APPLICATION_HOME =
            Paths.get(System.getProperty("user.dir"));

    @Override
    public Path getApplicationHome() {
        return APPLICATION_HOME;
    }

    @Override
    public Path getDatabaseDirectory() {
        return APPLICATION_HOME.resolve("database");
    }

    @Override
    public Path getDatabaseFile() {
        return getDatabaseDirectory().resolve("society.db");
    }

    @Override
    public Path getBackupDirectory() {
        return APPLICATION_HOME.resolve("backup");
    }

    @Override
    public Path getLogDirectory() {
        return APPLICATION_HOME.resolve("logs");
    }

    @Override
    public Path getReportsDirectory() {
        return APPLICATION_HOME.resolve("reports");
    }
}