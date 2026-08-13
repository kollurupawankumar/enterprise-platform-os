package com.clinical.lab.service;

import com.clinical.lab.entity.LabOrderEntity;
import com.clinical.lab.entity.LabOrderItemEntity;
import com.clinical.lab.entity.LabTestCatalogEntity;
import com.clinical.lab.repository.LabOrderItemRepository;
import com.clinical.lab.repository.LabOrderRepository;
import com.clinical.lab.repository.LabTestCatalogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface LabDiagnosticsService {
    LabOrderEntity createLabOrder(String visitId, String testName);
    LabOrderEntity createMultiTestOrder(String patientId, String visitId, String doctorName, List<LabTestCatalogEntity> tests, String paymentStatus, String paymentMode);
    LabOrderEntity updateLabStatus(String orderId, String status, String result);
    List<LabOrderEntity> getLabOrdersByVisit(String visitId);
    List<LabOrderEntity> getPendingLabOrders();
    List<LabOrderEntity> getAllOrders();
    List<LabTestCatalogEntity> getTestCatalog();
    List<LabOrderItemEntity> getOrderItems(String orderId);
    LabOrderItemEntity updateItemResult(Long itemId, String resultValue, String remarks, String status);
}

@Service
@Transactional
class LabDiagnosticsServiceImpl implements LabDiagnosticsService {

    private final LabOrderRepository labOrderRepository;
    private final LabOrderItemRepository labOrderItemRepository;
    private final LabTestCatalogRepository labTestCatalogRepository;
    private final com.clinical.admin.service.IdPatternGeneratorService idGenerator;

    public LabDiagnosticsServiceImpl(LabOrderRepository labOrderRepository,
                                      LabOrderItemRepository labOrderItemRepository,
                                      LabTestCatalogRepository labTestCatalogRepository,
                                      com.clinical.admin.service.IdPatternGeneratorService idGenerator) {
        this.labOrderRepository = labOrderRepository;
        this.labOrderItemRepository = labOrderItemRepository;
        this.labTestCatalogRepository = labTestCatalogRepository;
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
    public LabOrderEntity createMultiTestOrder(String patientId, String visitId, String doctorName, List<LabTestCatalogEntity> tests, String paymentStatus, String paymentMode) {
        LabOrderEntity order = new LabOrderEntity();
        long count = labOrderRepository.count() + 1;
        String orderId = idGenerator.generateId("LAB_ORDER_ID_FORMAT", "LAB-{YYYY}-{SEQ6}", count);
        order.setOrderId(orderId);
        order.setPatientId(patientId);
        order.setVisitId(visitId != null && !visitId.isBlank() ? visitId : "WALK-IN");
        order.setDoctorName(doctorName != null && !doctorName.isBlank() ? doctorName : "Self / Walk-in");
        order.setPaymentStatus(paymentStatus != null ? paymentStatus : "UNPAID");
        order.setPaymentMode(paymentMode != null ? paymentMode : "CASH");
        order.setStatus("ORDERED");

        BigDecimal total = BigDecimal.ZERO;
        List<String> names = new ArrayList<>();
        List<LabOrderItemEntity> items = new ArrayList<>();

        for (LabTestCatalogEntity test : tests) {
            total = total.add(test.getUnitPrice());
            names.add(test.getTestName());

            LabOrderItemEntity item = new LabOrderItemEntity();
            item.setOrderId(orderId);
            item.setTestCode(test.getTestCode());
            item.setTestName(test.getTestName());
            item.setPrice(test.getUnitPrice());
            item.setNormalRange(test.getNormalRange());
            item.setUnits(test.getUnits());
            item.setStatus("ORDERED");
            items.add(item);
        }

        order.setTotalAmount(total);
        order.setTestName(String.join(", ", names));
        LabOrderEntity savedOrder = labOrderRepository.save(order);

        List<LabOrderItemEntity> savedItems = labOrderItemRepository.saveAll(items);
        savedOrder.setItems(savedItems);
        return savedOrder;
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
        List<LabOrderEntity> orders = labOrderRepository.findByVisitId(visitId);
        for (LabOrderEntity o : orders) {
            o.setItems(labOrderItemRepository.findByOrderId(o.getOrderId()));
        }
        return orders;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LabOrderEntity> getPendingLabOrders() {
        List<LabOrderEntity> orders = labOrderRepository.findByStatus("ORDERED");
        for (LabOrderEntity o : orders) {
            o.setItems(labOrderItemRepository.findByOrderId(o.getOrderId()));
        }
        return orders;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LabOrderEntity> getAllOrders() {
        List<LabOrderEntity> orders = labOrderRepository.findAll();
        for (LabOrderEntity o : orders) {
            o.setItems(labOrderItemRepository.findByOrderId(o.getOrderId()));
        }
        return orders;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LabTestCatalogEntity> getTestCatalog() {
        return labTestCatalogRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LabOrderItemEntity> getOrderItems(String orderId) {
        return labOrderItemRepository.findByOrderId(orderId);
    }

    @Override
    public LabOrderItemEntity updateItemResult(Long itemId, String resultValue, String remarks, String status) {
        LabOrderItemEntity item = labOrderItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Lab order item not found: " + itemId));
        item.setResultValue(resultValue);
        item.setRemarks(remarks);
        if (status != null && !status.isEmpty()) {
            item.setStatus(status);
        }

        // Automatic High / Low Flag Detection
        if (resultValue != null && item.getNormalRange() != null && item.getNormalRange().contains("-")) {
            try {
                double val = Double.parseDouble(resultValue.trim());
                String[] parts = item.getNormalRange().split("-");
                double min = Double.parseDouble(parts[0].trim());
                double max = Double.parseDouble(parts[1].trim());
                if (val < min) item.setFlag("LOW");
                else if (val > max) item.setFlag("HIGH");
                else item.setFlag("NORMAL");
            } catch (Exception ignored) {
                item.setFlag("NORMAL");
            }
        }

        LabOrderItemEntity updatedItem = labOrderItemRepository.save(item);

        // Update Parent Lab Order Status if all items completed
        List<LabOrderItemEntity> allItems = labOrderItemRepository.findByOrderId(item.getOrderId());
        boolean allDone = allItems.stream().allMatch(i -> "COMPLETED".equalsIgnoreCase(i.getStatus()));
        if (allDone) {
            updateLabStatus(item.getOrderId(), "COMPLETED", "All parameters analyzed & recorded.");
        } else {
            updateLabStatus(item.getOrderId(), "IN_PROCESS", "Partial results recorded.");
        }

        return updatedItem;
    }
}

