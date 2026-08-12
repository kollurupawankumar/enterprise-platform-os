package com.clinical.prescription.service;

import com.clinical.prescription.entity.PrescriptionEntity;
import com.clinical.prescription.entity.PrescriptionItemEntity;
import com.clinical.prescription.repository.PrescriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final com.clinical.admin.service.IdPatternGeneratorService idGenerator;

    public PrescriptionService(PrescriptionRepository prescriptionRepository, com.clinical.admin.service.IdPatternGeneratorService idGenerator) {
        this.prescriptionRepository = prescriptionRepository;
        this.idGenerator = idGenerator;
    }

    public PrescriptionEntity createPrescription(String visitId, List<PrescriptionItemEntity> items) {
        PrescriptionEntity rx = new PrescriptionEntity();
        long count = prescriptionRepository.count() + 1;
        rx.setPrescriptionId(idGenerator.generateId("PRESCRIPTION_ID_FORMAT", "RX-{YYYY}-{SEQ6}", count));
        rx.setVisitId(visitId);
        rx.setItems(items);
        rx.setStatus("PENDING");
        return prescriptionRepository.save(rx);
    }
}
