package com.clinical.admin.service;

import com.clinical.audit.service.ClinicalAuditService;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;

@Service
public class DatabaseBackupService {

    private final ClinicalAuditService auditService;

    public DatabaseBackupService(ClinicalAuditService auditService) {
        this.auditService = auditService;
    }

    public String backupDatabase() {
        File sourceDb = new File("database/clinical.db");
        File backupDir = new File("backup");
        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }
        File targetDb = new File(backupDir, "clinical-backup-" + LocalDate.now() + ".db");
        try {
            if (sourceDb.exists()) {
                Files.copy(sourceDb.toPath(), targetDb.toPath(), StandardCopyOption.REPLACE_EXISTING);
                auditService.logEvent("ADMIN", "DATABASE_BACKUP", "Created snapshot: " + targetDb.getName());
                return targetDb.getAbsolutePath();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to backup database", e);
        }
        return "Database file not found";
    }
}
