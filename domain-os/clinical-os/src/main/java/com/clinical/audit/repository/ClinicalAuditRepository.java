package com.clinical.audit.repository;

import com.clinical.audit.entity.ClinicalAuditEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClinicalAuditRepository extends JpaRepository<ClinicalAuditEntity, Long> {
}
