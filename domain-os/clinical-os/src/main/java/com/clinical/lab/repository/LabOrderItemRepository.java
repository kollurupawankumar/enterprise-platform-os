package com.clinical.lab.repository;

import com.clinical.lab.entity.LabOrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LabOrderItemRepository extends JpaRepository<LabOrderItemEntity, Long> {
    List<LabOrderItemEntity> findByOrderId(String orderId);
}
