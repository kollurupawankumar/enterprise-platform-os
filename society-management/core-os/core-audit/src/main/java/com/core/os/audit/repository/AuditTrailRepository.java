package com.core.os.audit.repository;

import com.core.os.audit.entity.AuditTrailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditTrailRepository extends JpaRepository<AuditTrailEntity, Long> {
    List<AuditTrailEntity> findAllByOrderByTimestampDesc();
}
