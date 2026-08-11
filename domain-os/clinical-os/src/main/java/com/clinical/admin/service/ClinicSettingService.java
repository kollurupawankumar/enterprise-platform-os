package com.clinical.admin.service;

import com.clinical.audit.service.ClinicalAuditService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ClinicSettingService {

    private final JdbcTemplate jdbcTemplate;
    private final ClinicalAuditService auditService;

    public ClinicSettingService(JdbcTemplate jdbcTemplate, ClinicalAuditService auditService) {
        this.jdbcTemplate = jdbcTemplate;
        this.auditService = auditService;
    }

    public String getSetting(String key, String defaultValue) {
        try {
            String val = jdbcTemplate.queryForObject("SELECT setting_value FROM clinic_setting WHERE setting_key = ?", String.class, key);
            return val != null ? val : defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public void saveSetting(String key, String value) {
        jdbcTemplate.update("INSERT INTO clinic_setting (setting_key, setting_value) VALUES (?, ?) " +
                "ON CONFLICT(setting_key) DO UPDATE SET setting_value = EXCLUDED.setting_value", key, value);
        auditService.logEvent("ADMIN", "UPDATE_CLINIC_SETTING", "Key: " + key + ", Value: " + value);
    }
}
