package com.clinical.lab.service;

import com.clinical.lab.entity.LabOrderEntity;
import com.clinical.lab.repository.LabOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface LabDiagnosticsService {
    LabOrderEntity createLabOrder(String visitId, String testName);
    LabOrderEntity updateLabStatus(String orderId, String status, String result);
    List<LabOrderEntity> getLabOrdersByVisit(String visitId);
    List<LabOrderEntity> getPendingLabOrders();
}

@Service
@Transactional
class LabDiagnosticsServiceImpl implements LabDiagnosticsService {

    private final LabOrderRepository labOrderRepository;
    private final com.clinical.admin.service.IdPatternGeneratorService idGenerator;

    public LabDiagnosticsServiceImpl(LabOrderRepository labOrderRepository, com.clinical.admin.service.IdPatternGeneratorService idGenerator) {
        this.labOrderRepository = labOrderRepository;
        this.idGenerator = idGenerator;
    }

    @Override
    public LabOrderEntity createLabOrder(String visitId, String testName) {
        LabOrderEntity order = new LabOrderEntity();
        long count = labOrderRepository.count() + 1;
        order.setOrderId(idGenerator.generateId("LAB_ORDER_ID_FORMAT", "LAB-{YYYY}-{SEQ6}", count));
        order.setVisitId(visitId);
        order.setTestName(testName);
        order.setStatus("ORDERED");
        return labOrderRepository.save(order);
    }

    @Override
    public LabOrderEntity updateLabStatus(String orderId, String status, String result) {
        LabOrderEntity order = labOrderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Lab order not found: " + orderId));
        order.setStatus(status);
        if (result != null) {
            order.setResult(result);
        }
        return labOrderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LabOrderEntity> getLabOrdersByVisit(String visitId) {
        return labOrderRepository.findByVisitId(visitId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LabOrderEntity> getPendingLabOrders() {
        return labOrderRepository.findByStatus("ORDERED");
    }
}
