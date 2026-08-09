package com.society.finance.repository;

import com.society.finance.entity.AuditObservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditObservationRepository extends JpaRepository<AuditObservationEntity, Integer> {
}
