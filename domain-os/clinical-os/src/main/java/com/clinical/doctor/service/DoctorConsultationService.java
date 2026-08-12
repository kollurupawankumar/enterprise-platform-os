package com.clinical.doctor.service;

import com.clinical.doctor.entity.ClinicalEncounterEntity;
import com.clinical.doctor.repository.ClinicalEncounterRepository;
import com.clinical.visit.service.VisitService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface DoctorConsultationService {
    ClinicalEncounterEntity recordEncounter(ClinicalEncounterEntity encounter);
    Optional<ClinicalEncounterEntity> getEncounterByVisitId(String visitId);
}

@Service
@Transactional
class DoctorConsultationServiceImpl implements DoctorConsultationService {

    private final ClinicalEncounterRepository encounterRepository;
    private final VisitService visitService;
    private final com.clinical.admin.service.IdPatternGeneratorService idGenerator;

    public DoctorConsultationServiceImpl(ClinicalEncounterRepository encounterRepository, VisitService visitService, com.clinical.admin.service.IdPatternGeneratorService idGenerator) {
        this.encounterRepository = encounterRepository;
        this.visitService = visitService;
        this.idGenerator = idGenerator;
    }

    @Override
    public ClinicalEncounterEntity recordEncounter(ClinicalEncounterEntity encounter) {
        if (encounter.getEncounterId() == null || encounter.getEncounterId().isEmpty()) {
            long count = encounterRepository.count() + 1;
            encounter.setEncounterId(idGenerator.generateId("ENCOUNTER_ID_FORMAT", "ENC-{YYYY}-{SEQ6}", count));
        }
        ClinicalEncounterEntity saved = encounterRepository.save(encounter);
        // Automatically update visit status to IN_CONSULTATION or COMPLETED
        visitService.updateStatus(encounter.getVisitId(), "COMPLETED");
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClinicalEncounterEntity> getEncounterByVisitId(String visitId) {
        return encounterRepository.findByVisitId(visitId);
    }
}
