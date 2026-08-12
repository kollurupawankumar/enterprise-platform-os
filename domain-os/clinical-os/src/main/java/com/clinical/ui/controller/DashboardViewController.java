package com.clinical.ui.controller;

import com.clinical.billing.entity.InvoiceEntity;
import com.clinical.billing.repository.InvoiceRepository;
import com.clinical.lab.repository.LabOrderRepository;
import com.clinical.patient.repository.PatientRepository;
import com.clinical.pharmacy.entity.MedicineInventoryEntity;
import com.clinical.pharmacy.repository.MedicineInventoryRepository;
import com.clinical.visit.entity.VisitEntity;
import com.clinical.visit.repository.VisitRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DashboardViewController {

    private final PatientRepository patientRepository;
    private final VisitRepository visitRepository;
    private final InvoiceRepository invoiceRepository;
    private final LabOrderRepository labOrderRepository;
    private final MedicineInventoryRepository medicineRepository;

    @FXML private Label totalPatientsLabel;
    @FXML private Label activeVisitsLabel;
    @FXML private Label totalRevenueLabel;
    @FXML private Label completedLabsLabel;
    @FXML private Label lowStockLabel;

    @FXML private PieChart paymentPieChart;
    @FXML private BarChart<String, Number> footfallBarChart;

    public DashboardViewController(PatientRepository patientRepository,
                                  VisitRepository visitRepository,
                                  InvoiceRepository invoiceRepository,
                                  LabOrderRepository labOrderRepository,
                                  MedicineInventoryRepository medicineRepository) {
        this.patientRepository = patientRepository;
        this.visitRepository = visitRepository;
        this.invoiceRepository = invoiceRepository;
        this.labOrderRepository = labOrderRepository;
        this.medicineRepository = medicineRepository;
    }

    @FXML
    public void initialize() {
        loadKpis();
        loadCharts();
    }

    private void loadKpis() {
        long patientCount = patientRepository.count();
        if (totalPatientsLabel != null) totalPatientsLabel.setText(String.valueOf(patientCount));

        long visitCount = visitRepository.findAll().stream().filter(v -> !"CHECKED_OUT".equals(v.getStatus())).count();
        if (activeVisitsLabel != null) activeVisitsLabel.setText(String.valueOf(visitCount));

        BigDecimal totalRev = invoiceRepository.findAll().stream()
                .filter(i -> "PAID".equalsIgnoreCase(i.getPaymentStatus()))
                .map(InvoiceEntity::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (totalRevenueLabel != null) totalRevenueLabel.setText("₹ " + totalRev);

        long labCount = labOrderRepository.findAll().stream().filter(l -> "COMPLETED".equals(l.getStatus())).count();
        if (completedLabsLabel != null) completedLabsLabel.setText(String.valueOf(labCount));

        long lowStockCount = medicineRepository.findAll().stream()
                .filter(m -> m.getQuantity() != null && m.getReorderLevel() != null && m.getQuantity() <= m.getReorderLevel())
                .count();
        if (lowStockLabel != null) lowStockLabel.setText(String.valueOf(lowStockCount));
    }

    private void loadCharts() {
        // 1. Payment Pie Chart
        if (paymentPieChart != null) {
            Map<String, Double> modeMap = invoiceRepository.findAll().stream()
                    .filter(i -> i.getPaymentMode() != null)
                    .collect(Collectors.groupingBy(InvoiceEntity::getPaymentMode, Collectors.summingDouble(i -> i.getTotalAmount().doubleValue())));

            ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
            modeMap.forEach((mode, val) -> pieData.add(new PieChart.Data(mode, val)));
            paymentPieChart.setData(pieData);
        }

        // 2. Footfall Bar Chart
        if (footfallBarChart != null) {
            Map<String, Long> statusMap = visitRepository.findAll().stream()
                    .collect(Collectors.groupingBy(VisitEntity::getStatus, Collectors.counting()));

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Patients");
            statusMap.forEach((status, count) -> series.getData().add(new XYChart.Data<>(status, count)));

            footfallBarChart.getData().clear();
            footfallBarChart.getData().add(series);
        }
    }
}
