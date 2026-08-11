package com.clinical.ui.controller;

import com.clinical.lab.entity.LabOrderEntity;
import com.clinical.lab.service.LabDiagnosticsService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;

@Component
public class LabViewController {

    private final LabDiagnosticsService labDiagnosticsService;

    @FXML private TableView<LabOrderEntity> labOrderTable;
    @FXML private TableColumn<LabOrderEntity, String> orderIdCol;
    @FXML private TableColumn<LabOrderEntity, String> visitIdCol;
    @FXML private TableColumn<LabOrderEntity, String> testNameCol;
    @FXML private TableColumn<LabOrderEntity, String> statusCol;

    @FXML private TextField visitIdField;
    @FXML private TextField testNameField;

    public LabViewController(LabDiagnosticsService labDiagnosticsService) {
        this.labDiagnosticsService = labDiagnosticsService;
    }

    @FXML
    public void initialize() {
        if (orderIdCol != null) {
            orderIdCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getOrderId()));
            visitIdCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getVisitId()));
            testNameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTestName()));
            statusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));
        }
        loadLabOrders();
    }

    @FXML
    public void handleCreateLabOrder() {
        if (visitIdField == null || visitIdField.getText().isEmpty()) return;
        labDiagnosticsService.createLabOrder(visitIdField.getText(), testNameField.getText());
        loadLabOrders();
        visitIdField.clear();
        testNameField.clear();
    }

    private void loadLabOrders() {
        if (labOrderTable != null) {
            labOrderTable.setItems(FXCollections.observableArrayList(labDiagnosticsService.getPendingLabOrders()));
        }
    }
}
