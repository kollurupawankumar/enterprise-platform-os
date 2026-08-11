package com.clinical.ui.controller;

import com.clinical.admin.service.ClinicSettingService;
import com.clinical.admin.service.DatabaseBackupService;
import com.clinical.security.entity.UserEntity;
import com.clinical.security.repository.UserRepository;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;

@Component
public class AdministrationViewController {

    private final UserRepository userRepository;
    private final DatabaseBackupService backupService;
    private final ClinicSettingService clinicSettingService;

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField fullNameField;
    @FXML private ComboBox<String> roleCombo;

    @FXML private TextField clinicNameField;
    @FXML private TextField registrationNoField;
    @FXML private TextField idPrefixField;
    @FXML private TextField idFormatField;
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
        if (idPrefixField != null) {
            idPrefixField.setText(clinicSettingService.getSetting("PATIENT_ID_PREFIX", "PAT"));
        }
        if (idFormatField != null) {
            idFormatField.setText(clinicSettingService.getSetting("PATIENT_ID_FORMAT", "PAT-{YYYY}-{SEQ}"));
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
    }

    @FXML
    public void handleSaveClinicSettings() {
        if (idPrefixField != null && !idPrefixField.getText().isEmpty()) {
            clinicSettingService.saveSetting("PATIENT_ID_PREFIX", idPrefixField.getText());
        }
        if (idFormatField != null && !idFormatField.getText().isEmpty()) {
            clinicSettingService.saveSetting("PATIENT_ID_FORMAT", idFormatField.getText());
        }
        if (clinicNameField != null) {
            clinicSettingService.saveSetting("CLINIC_NAME", clinicNameField.getText());
        }
        if (registrationNoField != null) {
            clinicSettingService.saveSetting("REGISTRATION_NO", registrationNoField.getText());
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
