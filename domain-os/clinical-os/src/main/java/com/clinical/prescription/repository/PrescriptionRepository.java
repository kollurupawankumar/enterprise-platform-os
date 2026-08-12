package com.clinical.prescription.repository;

import com.clinical.prescription.entity.PrescriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrescriptionRepository extends JpaRepository<PrescriptionEntity, Long> {
    Optional<PrescriptionEntity> findByPrescriptionId(String prescriptionId);
    List<PrescriptionEntity> findByVisitId(String visitId);
}
