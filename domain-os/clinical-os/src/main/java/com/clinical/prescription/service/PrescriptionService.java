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

    public PrescriptionService(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    public PrescriptionEntity createPrescription(String visitId, List<PrescriptionItemEntity> items) {
        PrescriptionEntity rx = new PrescriptionEntity();
        long count = prescriptionRepository.count() + 1;
        rx.setPrescriptionId(String.format("RX-%06d", count));
        rx.setVisitId(visitId);
        rx.setItems(items);
        rx.setStatus("PENDING");
        return prescriptionRepository.save(rx);
    }
}
