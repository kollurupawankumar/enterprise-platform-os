package com.clinical.admin.service;

import com.clinical.audit.service.ClinicalAuditService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HospitalAdminService {

    private final ClinicalAuditService auditService;

    public HospitalAdminService(ClinicalAuditService auditService) {
        this.auditService = auditService;
    }

    public void configureHospitalProfile(String hospitalName, String registrationNo, String address) {
        auditService.logEvent("ADMIN", "UPDATE_HOSPITAL_PROFILE", "Updated profile for " + hospitalName);
    }

    public List<String> getAvailableRoles() {
        return List.of("ADMIN", "RECEPTIONIST", "DOCTOR", "LAB_TECHNICIAN", "PHARMACIST", "ACCOUNTANT");
    }
}
