package com.clinical.lab.repository;

import com.clinical.lab.entity.LabOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LabOrderRepository extends JpaRepository<LabOrderEntity, Long> {
    Optional<LabOrderEntity> findByOrderId(String orderId);
    List<LabOrderEntity> findByVisitId(String visitId);
    List<LabOrderEntity> findByStatus(String status);
}
