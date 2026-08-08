package com.core.os.storage.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {

    private final Path baseStoragePath = Paths.get("data", "uploads");

    public FileStorageService() {
        try {
            Files.createDirectories(baseStoragePath);
        } catch (IOException e) {
            System.err.println("Could not initialize upload storage directory: " + e.getMessage());
        }
    }

    public String storeFile(File sourceFile, String subFolder) throws IOException {
        Path targetDir = baseStoragePath.resolve(subFolder);
        Files.createDirectories(targetDir);

        String fileName = System.currentTimeMillis() + "_" + sourceFile.getName();
        Path targetPath = targetDir.resolve(fileName);

        Files.copy(sourceFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        return targetPath.toString();
    }

    public boolean deleteFile(String filePath) {
        if (filePath == null || filePath.isBlank()) return false;
        try {
            return Files.deleteIfExists(Paths.get(filePath));
        } catch (IOException e) {
            return false;
        }
    }
}
