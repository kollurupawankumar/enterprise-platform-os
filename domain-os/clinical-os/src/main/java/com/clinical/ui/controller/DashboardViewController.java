package com.clinical.ui.controller;

import com.clinical.doctor.repository.DoctorRepository;
import com.clinical.lab.repository.LabOrderRepository;
import com.clinical.patient.repository.PatientRepository;
import com.clinical.pharmacy.entity.MedicineInventoryEntity;
import com.clinical.pharmacy.repository.MedicineInventoryRepository;
import com.clinical.visit.entity.VisitEntity;
import com.clinical.visit.repository.VisitRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;

import java.util.List;
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

    @FXML private TableView<VisitEntity> queueSummaryTable;
    @FXML private TableColumn<VisitEntity, String> visitIdCol;
    @FXML private TableColumn<VisitEntity, String> patientIdCol;
    @FXML private TableColumn<VisitEntity, String> doctorCol;
    @FXML private TableColumn<VisitEntity, String> visitTypeCol;
    @FXML private TableColumn<VisitEntity, String> visitDateCol;
    @FXML private TableColumn<VisitEntity, String> statusCol;

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
        setupTable();
        loadQueueData();
        loadClinicalAlerts();
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

    private void setupTable() {
        if (queueSummaryTable != null && visitIdCol != null) {
            visitIdCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getVisitId()));
            patientIdCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPatientId()));
            doctorCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDoctorName()));
            visitTypeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getVisitType()));
            visitDateCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getVisitDate() != null ? data.getValue().getVisitDate().toString() : ""));
            statusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));
        }
    }

    private void loadQueueData() {
        if (queueSummaryTable != null) {
            List<VisitEntity> visits = visitRepository.findAll();
            queueSummaryTable.setItems(FXCollections.observableArrayList(visits));
        }
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
}
