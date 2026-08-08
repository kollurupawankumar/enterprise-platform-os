package com.society.compliance.repository;

import com.society.compliance.entity.ComplianceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplianceRepository extends JpaRepository<ComplianceEntity, Integer> {
    List<ComplianceEntity> findByStatus(String status);
}
