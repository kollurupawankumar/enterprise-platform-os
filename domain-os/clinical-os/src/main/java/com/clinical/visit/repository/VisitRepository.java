package com.clinical.visit.repository;

import com.clinical.visit.entity.VisitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VisitRepository extends JpaRepository<VisitEntity, Long> {
    Optional<VisitEntity> findByVisitId(String visitId);
    List<VisitEntity> findByPatientIdOrderByVisitDateDesc(String patientId);
    List<VisitEntity> findByStatus(String status);
}
