package com.core.os.config.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
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

    private static final Logger log = LoggerFactory.getLogger(BackupService.class);

    @Scheduled(cron = "0 0 23 * * ?") // Daily at 11:00 PM
    public void performScheduledDailyBackup() {
        log.info("Executing scheduled automated daily database backup...");
        try {
            createSystemBackup("database/society.db", "backups");
        } catch (Exception ex) {
            log.error("Scheduled automated backup failed: {}", ex.getMessage(), ex);
        }
    }

    public String createSystemBackup(String dbPath, String targetBackupDir) throws IOException {
        log.info("Initiating system database backup for path: {}", dbPath);

        File dbFile = new File(dbPath);
        if (!dbFile.exists()) {
            log.error("Database backup failed: File not found at {}", dbPath);
            throw new IllegalArgumentException("Database file not found at path: " + dbPath);
        }

        File backupDir = new File(targetBackupDir);
        if (!backupDir.exists()) {
            backupDir.mkdirs();
            log.info("Created backup directory: {}", targetBackupDir);
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

        log.info("System database backup completed successfully: {}", zipFile.getAbsolutePath());
        return zipFile.getAbsolutePath();
    }
}
