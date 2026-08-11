package com.clinical.audit.service;

import com.clinical.audit.entity.ClinicalAuditEntity;
import com.clinical.audit.repository.ClinicalAuditRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ClinicalAuditService {
    void logEvent(String actor, String action, String details);
    List<ClinicalAuditEntity> getRecentLogs();
}

@Service
@Transactional
class ClinicalAuditServiceImpl implements ClinicalAuditService {

    private final ClinicalAuditRepository auditRepository;

    public ClinicalAuditServiceImpl(ClinicalAuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    @Override
    public void logEvent(String actor, String action, String details) {
        ClinicalAuditEntity audit = new ClinicalAuditEntity();
        audit.setActor(actor);
        audit.setAction(action);
        audit.setDetails(details);
        auditRepository.save(audit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClinicalAuditEntity> getRecentLogs() {
        return auditRepository.findAll();
    }
}
