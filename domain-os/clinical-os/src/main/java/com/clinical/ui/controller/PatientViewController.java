package com.clinical.ui.controller;

import com.clinical.patient.entity.PatientEntity;
import com.clinical.patient.service.PatientService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;

@Component
public class PatientViewController {

    private final PatientService patientService;

    @FXML private TableView<PatientEntity> patientTable;
    @FXML private TableColumn<PatientEntity, String> idCol;
    @FXML private TableColumn<PatientEntity, String> nameCol;
    @FXML private TableColumn<PatientEntity, String> phoneCol;

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField phoneField;

    public PatientViewController(PatientService patientService) {
        this.patientService = patientService;
    }

    @FXML
    public void initialize() {
        if (idCol != null) {
            idCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPatientId()));
            nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFirstName() + " " + data.getValue().getLastName()));
            phoneCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPhone()));
        }
        loadPatients();
    }

    @FXML
    public void handleRegister() {
        if (firstNameField == null || firstNameField.getText().isEmpty()) return;
        PatientEntity p = new PatientEntity();
        p.setFirstName(firstNameField.getText());
        p.setLastName(lastNameField.getText());
        p.setPhone(phoneField.getText());
        patientService.registerPatient(p);
        loadPatients();
        firstNameField.clear();
        lastNameField.clear();
        phoneField.clear();
    }

    private void loadPatients() {
        if (patientTable != null) {
            patientTable.setItems(FXCollections.observableArrayList(patientService.getAllPatients()));
        }
    }
}
