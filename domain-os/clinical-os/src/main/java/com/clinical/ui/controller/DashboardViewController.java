package com.clinical.ui.controller;

import com.clinical.doctor.repository.DoctorRepository;
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
import javafx.scene.control.ListView;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DashboardViewController {

    private final PatientRepository patientRepository;
    private final VisitRepository visitRepository;
    private final DoctorRepository doctorRepository;
    private final LabOrderRepository labOrderRepository;
    private final MedicineInventoryRepository medicineRepository;

    @FXML private Label totalPatientsLabel;
    @FXML private Label activeVisitsLabel;
    @FXML private Label totalDoctorsLabel;
    @FXML private Label completedLabsLabel;
    @FXML private Label lowStockLabel;
    @FXML private Label pendingConsultationsLabel;

    @FXML private BarChart<String, Number> footfallBarChart;
    @FXML private ListView<String> criticalStockListView;

    public DashboardViewController(PatientRepository patientRepository,
                                  VisitRepository visitRepository,
                                  DoctorRepository doctorRepository,
                                  LabOrderRepository labOrderRepository,
                                  MedicineInventoryRepository medicineRepository) {
        this.patientRepository = patientRepository;
        this.visitRepository = visitRepository;
        this.doctorRepository = doctorRepository;
        this.labOrderRepository = labOrderRepository;
        this.medicineRepository = medicineRepository;
    }

    @FXML
    public void initialize() {
        loadKpis();
        loadClinicalAlerts();
        loadCharts();
    }

    private void loadKpis() {
        long patientCount = patientRepository.count();
        if (totalPatientsLabel != null) totalPatientsLabel.setText(String.valueOf(patientCount));

        long activeQueue = visitRepository.findAll().stream().filter(v -> !"CHECKED_OUT".equals(v.getStatus()) && !"COMPLETED".equals(v.getStatus())).count();
        if (activeVisitsLabel != null) activeVisitsLabel.setText(String.valueOf(activeQueue));

        long doctorCount = doctorRepository.count();
        if (totalDoctorsLabel != null) totalDoctorsLabel.setText(String.valueOf(doctorCount));

        long labCount = labOrderRepository.findAll().stream().filter(l -> "COMPLETED".equals(l.getStatus())).count();
        if (completedLabsLabel != null) completedLabsLabel.setText(String.valueOf(labCount));

        List<MedicineInventoryEntity> lowStockMeds = medicineRepository.findAll().stream()
                .filter(m -> m.getQuantity() != null && m.getReorderLevel() != null && m.getQuantity() <= m.getReorderLevel())
                .collect(Collectors.toList());

        if (lowStockLabel != null) lowStockLabel.setText(String.valueOf(lowStockMeds.size()));
    }

    private void loadClinicalAlerts() {
        if (criticalStockListView != null) {
            ObservableList<String> items = FXCollections.observableArrayList();
            medicineRepository.findAll().stream()
                    .filter(m -> m.getQuantity() != null && m.getReorderLevel() != null && m.getQuantity() <= m.getReorderLevel())
                    .forEach(m -> items.add("⚠️ " + m.getMedicineName() + " — Qty: " + m.getQuantity() + " (Reorder: " + m.getReorderLevel() + ")"));

            if (items.isEmpty()) {
                items.add("✅ All pharmacy medicine stock levels are healthy.");
            }
            criticalStockListView.setItems(items);
        }

        long waitingDoctors = visitRepository.findAll().stream().filter(v -> "WAITING".equals(v.getStatus()) || "IN_CONSULTATION".equals(v.getStatus())).count();
        if (pendingConsultationsLabel != null) {
            pendingConsultationsLabel.setText(waitingDoctors + " Patient consultations pending note");
        }
    }

    private void loadCharts() {
        if (footfallBarChart != null) {
            Map<String, Long> statusMap = visitRepository.findAll().stream()
                    .collect(Collectors.groupingBy(VisitEntity::getStatus, Collectors.counting()));

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Patients");
            statusMap.forEach((status, count) -> series.getData().add(new XYChart.Data<>(status, count)));

            footfallBarChart.setData(FXCollections.observableArrayList(List.of(series)));
        }
    }
}
