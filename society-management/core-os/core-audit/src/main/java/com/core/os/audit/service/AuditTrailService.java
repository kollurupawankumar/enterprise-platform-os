package com.core.os.audit.service;

import com.core.os.audit.entity.AuditTrailEntity;
import com.core.os.audit.repository.AuditTrailRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditTrailService {

    private final AuditTrailRepository auditTrailRepository;

    public AuditTrailService(AuditTrailRepository auditTrailRepository) {
        this.auditTrailRepository = auditTrailRepository;
    }

    public void logAction(String entityName, Long entityId, String action, String performedBy, String details) {
        AuditTrailEntity audit = new AuditTrailEntity();
        audit.setEntityName(entityName);
        audit.setEntityId(entityId);
        audit.setAction(action);
        audit.setPerformedBy(performedBy);
        audit.setDetails(details);
        auditTrailRepository.save(audit);
    }

    public List<AuditTrailEntity> getRecentAudits() {
        return auditTrailRepository.findAllByOrderByTimestampDesc();
    }
}
