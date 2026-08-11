package com.clinical.ui.controller;

import com.clinical.visit.entity.VisitEntity;
import com.clinical.visit.service.VisitService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;

@Component
public class VisitViewController {

    private final VisitService visitService;

    @FXML private TableView<VisitEntity> visitQueueTable;
    @FXML private TableColumn<VisitEntity, String> visitIdCol;
    @FXML private TableColumn<VisitEntity, String> patientCol;
    @FXML private TableColumn<VisitEntity, String> doctorCol;
    @FXML private TableColumn<VisitEntity, String> statusCol;

    @FXML private TextField patientIdField;
    @FXML private TextField doctorNameField;

    public VisitViewController(VisitService visitService) {
        this.visitService = visitService;
    }

    @FXML
    public void initialize() {
        if (visitIdCol != null) {
            visitIdCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getVisitId()));
            patientCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPatientId()));
            doctorCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDoctorName()));
            statusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));
        }
        loadQueue();
    }

    @FXML
    public void handleCreateVisit() {
        if (patientIdField == null || patientIdField.getText().isEmpty()) return;
        visitService.createVisit(patientIdField.getText(), doctorNameField.getText(), "OPD");
        loadQueue();
        patientIdField.clear();
        doctorNameField.clear();
    }

    private void loadQueue() {
        if (visitQueueTable != null) {
            visitQueueTable.setItems(FXCollections.observableArrayList(visitService.getQueueByStatus("WAITING")));
        }
    }
}
