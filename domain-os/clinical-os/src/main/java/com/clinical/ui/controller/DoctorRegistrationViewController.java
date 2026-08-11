package com.clinical.ui.controller;

import com.clinical.doctor.entity.DoctorEntity;
import com.clinical.doctor.service.DoctorService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DoctorRegistrationViewController {

    private final DoctorService doctorService;

    @FXML private TableView<DoctorEntity> doctorTable;
    @FXML private TableColumn<DoctorEntity, String> idCol;
    @FXML private TableColumn<DoctorEntity, String> nameCol;
    @FXML private TableColumn<DoctorEntity, String> specCol;
    @FXML private TableColumn<DoctorEntity, String> phoneCol;
    @FXML private TableColumn<DoctorEntity, BigDecimal> feeCol;

    @FXML private TextField nameField;
    @FXML private TextField specializationField;
    @FXML private TextField licenseField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextField feeField;

    public DoctorRegistrationViewController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @FXML
    public void initialize() {
        if (idCol != null) {
            idCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDoctorId()));
            nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
            specCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSpecialization()));
            phoneCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPhone()));
            feeCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getConsultationFee()));
        }
        loadDoctors();
    }

    @FXML
    public void handleSaveDoctor() {
        if (nameField == null || nameField.getText().isEmpty()) return;
        DoctorEntity doc = new DoctorEntity();
        doc.setName(nameField.getText());
        doc.setSpecialization(specializationField.getText());
        doc.setLicenseNumber(licenseField.getText());
        doc.setPhone(phoneField.getText());
        doc.setEmail(emailField.getText());
        if (feeField != null && !feeField.getText().isEmpty()) {
            doc.setConsultationFee(new BigDecimal(feeField.getText()));
        }
        doctorService.registerDoctor(doc);
        loadDoctors();
        clearFields();
    }

    private void loadDoctors() {
        if (doctorTable != null) {
            doctorTable.setItems(FXCollections.observableArrayList(doctorService.getAllDoctors()));
        }
    }

    private void clearFields() {
        if (nameField != null) nameField.clear();
        if (specializationField != null) specializationField.clear();
        if (licenseField != null) licenseField.clear();
        if (phoneField != null) phoneField.clear();
        if (emailField != null) emailField.clear();
        if (feeField != null) feeField.clear();
    }
}
