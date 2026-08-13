package com.clinical.doctor.repository;

import com.clinical.doctor.entity.ClinicalEncounterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClinicalEncounterRepository extends JpaRepository<ClinicalEncounterEntity, Long> {
    Optional<ClinicalEncounterEntity> findByVisitId(String visitId);
    List<ClinicalEncounterEntity> findByPatientId(String patientId);
    List<ClinicalEncounterEntity> findByDoctorId(String doctorId);
}

