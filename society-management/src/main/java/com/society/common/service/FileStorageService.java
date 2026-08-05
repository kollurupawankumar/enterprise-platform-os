package com.society.common.service;

import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final String UPLOAD_ROOT_DIR = "uploads/members";

    public FileStorageService() {
        File dir = new File(UPLOAD_ROOT_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * Copies a source file to local uploads storage directory and returns relative path.
     *
     * @param sourceFile File selected by user
     * @param prefix Sub-folder or prefix name (e.g. "photos", "kyc")
     * @return Relative path string stored in DB
     */
    public String storeFile(File sourceFile, String prefix) throws IOException {
        if (sourceFile == null || !sourceFile.exists()) {
            return null;
        }

        String subDirName = UPLOAD_ROOT_DIR + "/" + prefix;
        File subDir = new File(subDirName);
        if (!subDir.exists()) {
            subDir.mkdirs();
        }

        String originalName = sourceFile.getName();
        String extension = "";
        int i = originalName.lastIndexOf('.');
        if (i > 0) {
            extension = originalName.substring(i);
        }

        String uniqueFileName = UUID.randomUUID().toString().substring(0, 8) + "_" + originalName;
        Path targetPath = Paths.get(subDirName, uniqueFileName);

        Files.copy(sourceFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        return targetPath.toString().replace("\\", "/");
    }

    /**
     * Copies a source file to local storage directory with a custom specified filename.
     */
    public String storeFileWithName(File sourceFile, String prefix, String targetFileName) throws IOException {
        if (sourceFile == null || !sourceFile.exists() || targetFileName == null || targetFileName.isBlank()) {
            return null;
        }

        String subDirName = UPLOAD_ROOT_DIR + "/" + prefix;
        File subDir = new File(subDirName);
        if (!subDir.exists()) {
            subDir.mkdirs();
        }

        Path targetPath = Paths.get(subDirName, targetFileName);
        Files.copy(sourceFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        return targetPath.toString().replace("\\", "/");
    }
}
