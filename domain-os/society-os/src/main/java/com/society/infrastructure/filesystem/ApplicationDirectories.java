package com.society.infrastructure.filesystem;

import java.nio.file.Path;

public interface ApplicationDirectories {

    Path getApplicationHome();

    Path getDatabaseDirectory();

    Path getDatabaseFile();

    Path getBackupDirectory();

    Path getLogDirectory();

    Path getReportsDirectory();

}