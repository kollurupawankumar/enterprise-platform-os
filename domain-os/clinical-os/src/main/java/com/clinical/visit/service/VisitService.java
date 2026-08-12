package com.clinical.visit.service;

import com.clinical.visit.entity.VisitEntity;
import com.clinical.visit.repository.VisitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface VisitService {
    VisitEntity createVisit(String patientId, String doctorName, String visitType);
    Optional<VisitEntity> findByVisitId(String visitId);
    List<VisitEntity> getPatientVisits(String patientId);
    List<VisitEntity> getQueueByStatus(String status);
    List<VisitEntity> getActiveVisits();
    VisitEntity updateStatus(String visitId, String status);
}

@Service
@Transactional
class VisitServiceImpl implements VisitService {

    private final VisitRepository visitRepository;
    private final com.clinical.admin.service.IdPatternGeneratorService idGenerator;

    public VisitServiceImpl(VisitRepository visitRepository, com.clinical.admin.service.IdPatternGeneratorService idGenerator) {
        this.visitRepository = visitRepository;
        this.idGenerator = idGenerator;
    }

    @Override
    public VisitEntity createVisit(String patientId, String doctorName, String visitType) {
        VisitEntity visit = new VisitEntity();
        long count = visitRepository.count() + 1;
        visit.setVisitId(idGenerator.generateId("VISIT_ID_FORMAT", "VISIT-{YYYY}-{SEQ6}", count));
        visit.setPatientId(patientId);
        visit.setDoctorName(doctorName);
        visit.setVisitDate(LocalDate.now());
        visit.setVisitType(visitType != null ? visitType : "OPD");
        visit.setStatus("WAITING");
        return visitRepository.save(visit);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VisitEntity> findByVisitId(String visitId) {
        return visitRepository.findByVisitId(visitId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VisitEntity> getPatientVisits(String patientId) {
        return visitRepository.findByPatientIdOrderByVisitDateDesc(patientId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VisitEntity> getQueueByStatus(String status) {
        return visitRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VisitEntity> getActiveVisits() {
        return visitRepository.findAll();
    }

    @Override
    public VisitEntity updateStatus(String visitId, String status) {
        VisitEntity visit = visitRepository.findByVisitId(visitId)
                .orElseThrow(() -> new IllegalArgumentException("Visit not found: " + visitId));
        visit.setStatus(status);
        return visitRepository.save(visit);
    }
}
