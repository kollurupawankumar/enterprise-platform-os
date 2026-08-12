package com.clinical.admin.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class IdPatternGeneratorService {

    private final ClinicSettingService clinicSettingService;

    public IdPatternGeneratorService(ClinicSettingService clinicSettingService) {
        this.clinicSettingService = clinicSettingService;
    }

    public String generateId(String settingKey, String defaultPattern, long sequenceNumber) {
        String pattern = clinicSettingService.getSetting(settingKey, defaultPattern);
        String year = String.valueOf(LocalDate.now().getYear());
        String seq6 = String.format("%06d", sequenceNumber);
        String seq4 = String.format("%04d", sequenceNumber);

        return pattern.replace("{YYYY}", year)
                .replace("{YEAR}", year)
                .replace("{SEQ}", seq6)
                .replace("{SEQ6}", seq6)
                .replace("{SEQ4}", seq4);
    }
}
