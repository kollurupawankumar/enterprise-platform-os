package com.clinical.document.repository;

import com.clinical.document.entity.PatientDocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientDocumentRepository extends JpaRepository<PatientDocumentEntity, Long> {
    Optional<PatientDocumentEntity> findByDocumentId(String documentId);
    List<PatientDocumentEntity> findByPatientId(String patientId);
    List<PatientDocumentEntity> findByVisitId(String visitId);
}
