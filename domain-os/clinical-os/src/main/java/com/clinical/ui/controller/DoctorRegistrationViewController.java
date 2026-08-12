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

    @FXML private ComboBox<String> titleCombo;
    @FXML private TextField firstNameField;
    @FXML private TextField middleNameField;
    @FXML private TextField lastNameField;
    @FXML private ComboBox<String> genderCombo;

    @FXML private TextField licenseField;
    @FXML private TextField regAuthorityField;
    @FXML private ComboBox<String> specialtyCombo;
    @FXML private TextField qualificationField;

    @FXML private TextField phoneField;
    @FXML private TextField altPhoneField;
    @FXML private TextField emailField;

    @FXML private TextField feeField;
    @FXML private ComboBox<String> statusCombo;

    @FXML private TableView<DoctorEntity> doctorTable;
    @FXML private TableColumn<DoctorEntity, String> idCol;
    @FXML private TableColumn<DoctorEntity, String> nameCol;
    @FXML private TableColumn<DoctorEntity, String> specCol;
    @FXML private TableColumn<DoctorEntity, String> regCol;
    @FXML private TableColumn<DoctorEntity, String> phoneCol;
    @FXML private TableColumn<DoctorEntity, BigDecimal> feeCol;

    public DoctorRegistrationViewController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @FXML
    public void initialize() {
        setupComboBoxes();
        setupTableColumns();
        loadDoctors();
    }

    private void setupComboBoxes() {
        if (titleCombo != null) titleCombo.setItems(FXCollections.observableArrayList("Dr.", "Prof. Dr."));
        if (genderCombo != null) genderCombo.setItems(FXCollections.observableArrayList("Male", "Female", "Other"));
        if (specialtyCombo != null) specialtyCombo.setItems(FXCollections.observableArrayList(
                "General Medicine", "Pediatrics", "Cardiology", "Dermatology", "Orthopedics", "Gynecology", "ENT", "Ophthalmology", "Dentistry"
        ));
        if (statusCombo != null) statusCombo.setItems(FXCollections.observableArrayList("ACTIVE", "PENDING", "INACTIVE", "SUSPENDED", "TERMINATED"));

        if (titleCombo != null) titleCombo.getSelectionModel().select("Dr.");
        if (specialtyCombo != null) specialtyCombo.getSelectionModel().select("General Medicine");
        if (statusCombo != null) statusCombo.getSelectionModel().select("ACTIVE");
    }

    private void setupTableColumns() {
        if (idCol != null) {
            idCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDoctorId()));
            nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDisplayName() != null ? data.getValue().getDisplayName() : data.getValue().getName()));
            specCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSpecialization()));
            regCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLicenseNumber()));
            phoneCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPhone()));
            feeCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getConsultationFee()));
        }
    }

    @FXML
    public void handleSaveDoctor() {
        if (firstNameField == null || firstNameField.getText().isEmpty() || licenseField == null || licenseField.getText().isEmpty()) return;

        DoctorEntity doc = new DoctorEntity();
        doc.setTitle(titleCombo != null ? titleCombo.getValue() : "Dr.");
        doc.setFirstName(firstNameField.getText());
        doc.setMiddleName(middleNameField != null ? middleNameField.getText() : "");
        doc.setLastName(lastNameField != null ? lastNameField.getText() : "");
        doc.setDisplayName(doc.getTitle() + " " + doc.getFirstName() + " " + doc.getLastName());
        doc.setName(doc.getDisplayName());
        doc.setGender(genderCombo != null ? genderCombo.getValue() : "Male");

        doc.setLicenseNumber(licenseField.getText());
        if (regAuthorityField != null) doc.setRegistrationAuthority(regAuthorityField.getText());
        if (specialtyCombo != null) doc.setSpecialization(specialtyCombo.getValue());
        if (qualificationField != null) doc.setQualification(qualificationField.getText());

        if (phoneField != null) doc.setPhone(phoneField.getText());
        if (altPhoneField != null) doc.setAlternatePhone(altPhoneField.getText());
        if (emailField != null) doc.setEmail(emailField.getText());

        if (feeField != null && !feeField.getText().isEmpty()) {
            doc.setConsultationFee(new BigDecimal(feeField.getText()));
        }
        if (statusCombo != null) doc.setLifecycleStatus(statusCombo.getValue());

        doctorService.registerDoctor(doc);
        loadDoctors();
        handleClearForm();
    }

    @FXML
    public void handleClearForm() {
        if (firstNameField != null) firstNameField.clear();
        if (middleNameField != null) middleNameField.clear();
        if (lastNameField != null) lastNameField.clear();
        if (licenseField != null) licenseField.clear();
        if (regAuthorityField != null) regAuthorityField.clear();
        if (qualificationField != null) qualificationField.clear();
        if (phoneField != null) phoneField.clear();
        if (altPhoneField != null) altPhoneField.clear();
        if (emailField != null) emailField.clear();
        if (feeField != null) feeField.setText("500");
    }

    private void loadDoctors() {
        if (doctorTable != null) {
            doctorTable.setItems(FXCollections.observableArrayList(doctorService.getAllDoctors()));
        }
    }
}
