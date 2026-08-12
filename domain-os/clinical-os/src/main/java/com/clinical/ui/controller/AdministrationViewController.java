package com.clinical.ui.controller;

import com.clinical.admin.service.ClinicSettingService;
import com.clinical.admin.service.DatabaseBackupService;
import com.clinical.security.entity.UserEntity;
import com.clinical.security.repository.UserRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class AdministrationViewController {

    private final UserRepository userRepository;
    private final DatabaseBackupService backupService;
    private final ClinicSettingService clinicSettingService;

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField fullNameField;
    @FXML private ComboBox<String> roleCombo;

    @FXML private TableView<UserEntity> userTable;
    @FXML private TableColumn<UserEntity, String> usernameCol;
    @FXML private TableColumn<UserEntity, String> fullNameCol;
    @FXML private TableColumn<UserEntity, String> roleCol;
    @FXML private TableColumn<UserEntity, String> passwordCol;

    @FXML private TextField clinicNameField;
    @FXML private TextField logoPathField;
    @FXML private ImageView logoPreviewImageView;
    @FXML private TextField registrationNoField;
    @FXML private TextField clinicAddressField;
    @FXML private TextField clinicPhoneField;
    @FXML private TextField clinicEmailField;
    @FXML private TextField patientIdFormatField;
    @FXML private TextField doctorIdFormatField;
    @FXML private TextField visitIdFormatField;
    @FXML private TextField encounterIdFormatField;
    @FXML private TextField invoiceIdFormatField;
    @FXML private TextField labOrderIdFormatField;
    @FXML private TextField prescriptionIdFormatField;
    @FXML private TextField referralIdFormatField;
    @FXML private Label backupStatusLabel;

    public AdministrationViewController(UserRepository userRepository,
                                        DatabaseBackupService backupService,
                                        ClinicSettingService clinicSettingService) {
        this.userRepository = userRepository;
        this.backupService = backupService;
        this.clinicSettingService = clinicSettingService;
    }

    @FXML
    public void initialize() {
        if (roleCombo != null) {
            roleCombo.setItems(FXCollections.observableArrayList(
                    "ADMIN", "RECEPTIONIST", "DOCTOR", "LAB_TECHNICIAN", "PHARMACIST", "ACCOUNTANT"
            ));
        }
        if (clinicNameField != null) {
            clinicNameField.setText(clinicSettingService.getSetting("CLINIC_NAME", "Apex Multispecialty Clinic"));
        }
        if (logoPathField != null) {
            String logoPath = clinicSettingService.getSetting("CLINIC_LOGO_PATH", "images/default_logo.png");
            logoPathField.setText(logoPath);
            loadLogoPreview(logoPath);
        }
        if (registrationNoField != null) {
            registrationNoField.setText(clinicSettingService.getSetting("REGISTRATION_NO", "REG-2026-CLINIC-88"));
        }
        if (clinicAddressField != null) {
            clinicAddressField.setText(clinicSettingService.getSetting("ADDRESS", "123 Healthcare Boulevard, Tech City"));
        }
        if (clinicPhoneField != null) {
            clinicPhoneField.setText(clinicSettingService.getSetting("PHONE", "+91 9988776655"));
        }
        if (clinicEmailField != null) {
            clinicEmailField.setText(clinicSettingService.getSetting("EMAIL", "contact@apexclinic.com"));
        }
        if (patientIdFormatField != null) {
            patientIdFormatField.setText(clinicSettingService.getSetting("PATIENT_ID_FORMAT", "PAT-{YYYY}-{SEQ}"));
        }
        if (doctorIdFormatField != null) {
            doctorIdFormatField.setText(clinicSettingService.getSetting("DOCTOR_ID_FORMAT", "DOC-{SEQ6}"));
        }
        if (visitIdFormatField != null) {
            visitIdFormatField.setText(clinicSettingService.getSetting("VISIT_ID_FORMAT", "VISIT-{YYYY}-{SEQ6}"));
        }
        if (encounterIdFormatField != null) {
            encounterIdFormatField.setText(clinicSettingService.getSetting("ENCOUNTER_ID_FORMAT", "ENC-{YYYY}-{SEQ6}"));
        }
        if (invoiceIdFormatField != null) {
            invoiceIdFormatField.setText(clinicSettingService.getSetting("INVOICE_ID_FORMAT", "INV-{YYYY}-{SEQ6}"));
        }
        if (labOrderIdFormatField != null) {
            labOrderIdFormatField.setText(clinicSettingService.getSetting("LAB_ORDER_ID_FORMAT", "LAB-{YYYY}-{SEQ6}"));
        }
        if (prescriptionIdFormatField != null) {
            prescriptionIdFormatField.setText(clinicSettingService.getSetting("PRESCRIPTION_ID_FORMAT", "RX-{YYYY}-{SEQ6}"));
        }
        if (referralIdFormatField != null) {
            referralIdFormatField.setText(clinicSettingService.getSetting("REFERRAL_ID_FORMAT", "REF-{YYYY}-{SEQ6}"));
        }
        setupTableColumns();
        loadUsers();
    }

    @FXML
    public void handleBrowseLogo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Clinic Logo Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            String path = selectedFile.getAbsolutePath();
            if (logoPathField != null) logoPathField.setText(path);
            loadLogoPreview(path);
        }
    }

    private void loadLogoPreview(String path) {
        try {
            Image image;
            if (path.startsWith("images/")) {
                image = new Image(getClass().getResourceAsStream("/" + path));
            } else {
                image = new Image(new File(path).toURI().toString());
            }
            if (logoPreviewImageView != null) logoPreviewImageView.setImage(image);
        } catch (Exception e) {
            // Ignore preview load error
        }
    }

    private void setupTableColumns() {
        if (userTable != null && usernameCol != null) {
            usernameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUsername()));
            fullNameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFullName()));
            roleCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRole()));
            passwordCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPasswordHash()));
        }
    }

    private void loadUsers() {
        if (userTable != null) {
            userTable.setItems(FXCollections.observableArrayList(userRepository.findAll()));
        }
    }

    @FXML
    public void handleCreateUser() {
        if (usernameField == null || usernameField.getText().isEmpty()) return;
        UserEntity user = new UserEntity();
        user.setUsername(usernameField.getText());
        user.setPasswordHash(passwordField.getText());
        user.setFullName(fullNameField.getText());
        user.setRole(roleCombo.getValue() != null ? roleCombo.getValue() : "RECEPTIONIST");
        userRepository.save(user);

        usernameField.clear();
        passwordField.clear();
        fullNameField.clear();
        loadUsers();
    }

    @FXML
    public void handleSaveClinicSettings() {
        if (clinicNameField != null) {
            clinicSettingService.saveSetting("CLINIC_NAME", clinicNameField.getText());
        }
        if (logoPathField != null) {
            clinicSettingService.saveSetting("CLINIC_LOGO_PATH", logoPathField.getText());
        }
        if (registrationNoField != null) {
            clinicSettingService.saveSetting("REGISTRATION_NO", registrationNoField.getText());
        }
        if (clinicAddressField != null) {
            clinicSettingService.saveSetting("ADDRESS", clinicAddressField.getText());
        }
        if (clinicPhoneField != null) {
            clinicSettingService.saveSetting("PHONE", clinicPhoneField.getText());
        }
        if (clinicEmailField != null) {
            clinicSettingService.saveSetting("EMAIL", clinicEmailField.getText());
        }
        if (patientIdFormatField != null) {
            clinicSettingService.saveSetting("PATIENT_ID_FORMAT", patientIdFormatField.getText());
        }
        if (doctorIdFormatField != null) {
            clinicSettingService.saveSetting("DOCTOR_ID_FORMAT", doctorIdFormatField.getText());
        }
        if (visitIdFormatField != null) {
            clinicSettingService.saveSetting("VISIT_ID_FORMAT", visitIdFormatField.getText());
        }
        if (encounterIdFormatField != null) {
            clinicSettingService.saveSetting("ENCOUNTER_ID_FORMAT", encounterIdFormatField.getText());
        }
        if (invoiceIdFormatField != null) {
            clinicSettingService.saveSetting("INVOICE_ID_FORMAT", invoiceIdFormatField.getText());
        }
        if (labOrderIdFormatField != null) {
            clinicSettingService.saveSetting("LAB_ORDER_ID_FORMAT", labOrderIdFormatField.getText());
        }
        if (prescriptionIdFormatField != null) {
            clinicSettingService.saveSetting("PRESCRIPTION_ID_FORMAT", prescriptionIdFormatField.getText());
        }
        if (referralIdFormatField != null) {
            clinicSettingService.saveSetting("REFERRAL_ID_FORMAT", referralIdFormatField.getText());
        }
    }

    @FXML
    public void handleBackup() {
        String result = backupService.backupDatabase();
        if (backupStatusLabel != null) {
            backupStatusLabel.setText("Backup Saved: " + result);
        }
    }
}
