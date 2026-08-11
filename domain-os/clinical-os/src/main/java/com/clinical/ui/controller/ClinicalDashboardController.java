package com.clinical.ui.controller;

import com.clinical.patient.entity.PatientEntity;
import com.clinical.patient.service.PatientService;
import com.clinical.visit.entity.VisitEntity;
import com.clinical.visit.service.VisitService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;

@Component
public class ClinicalDashboardController {

    private final PatientService patientService;
    private final VisitService visitService;

    @FXML private TableView<PatientEntity> patientTable;
    @FXML private TableColumn<PatientEntity, String> idCol;
    @FXML private TableColumn<PatientEntity, String> nameCol;
    @FXML private TableColumn<PatientEntity, String> phoneCol;

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField phoneField;
    @FXML private ComboBox<String> genderCombo;

    public ClinicalDashboardController(PatientService patientService, VisitService visitService) {
        this.patientService = patientService;
        this.visitService = visitService;
    }

    @FXML
    public void initialize() {
        if (genderCombo != null) {
            genderCombo.setItems(FXCollections.observableArrayList("Male", "Female", "Other"));
        }
        loadPatients();
    }

    @FXML
    public void handleRegisterPatient() {
        if (firstNameField == null || firstNameField.getText().isEmpty()) return;
        PatientEntity p = new PatientEntity();
        p.setFirstName(firstNameField.getText());
        p.setLastName(lastNameField.getText());
        p.setPhone(phoneField.getText());
        p.setGender(genderCombo != null ? genderCombo.getValue() : "Male");
        patientService.registerPatient(p);
        loadPatients();
        clearFields();
    }

    private void loadPatients() {
        if (patientTable != null) {
            patientTable.setItems(FXCollections.observableArrayList(patientService.getAllPatients()));
        }
    }

    private void clearFields() {
        if (firstNameField != null) firstNameField.clear();
        if (lastNameField != null) lastNameField.clear();
        if (phoneField != null) phoneField.clear();
    }
}
