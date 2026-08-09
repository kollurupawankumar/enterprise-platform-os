package com.core.os.config.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class BackupService {

    public String createSystemBackup(String dbPath, String targetBackupDir) throws IOException {
        File dbFile = new File(dbPath);
        if (!dbFile.exists()) {
            throw new IllegalArgumentException("Database file not found at path: " + dbPath);
        }

        File backupDir = new File(targetBackupDir);
        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String zipFileName = "backup_" + timestamp + ".zip";
        File zipFile = Paths.get(targetBackupDir, zipFileName).toFile();

        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zos = new ZipOutputStream(fos);
             FileInputStream fis = new FileInputStream(dbFile)) {

            ZipEntry zipEntry = new ZipEntry(dbFile.getName());
            zos.putNextEntry(zipEntry);

            byte[] buffer = new byte[8192];
            int length;
            while ((length = fis.read(buffer)) >= 0) {
                zos.write(buffer, 0, length);
            }
            zos.closeEntry();
        }

        return zipFile.getAbsolutePath();
    }
}
