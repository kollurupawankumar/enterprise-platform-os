package com.clinical.ui.controller;

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

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField fullNameField;
    @FXML private ComboBox<String> roleCombo;

    @FXML private TextField clinicNameField;
    @FXML private TextField registrationNoField;
    @FXML private Label backupStatusLabel;

    public AdministrationViewController(UserRepository userRepository, DatabaseBackupService backupService) {
        this.userRepository = userRepository;
        this.backupService = backupService;
    }

    @FXML
    public void initialize() {
        if (roleCombo != null) {
            roleCombo.setItems(FXCollections.observableArrayList(
                    "ADMIN", "RECEPTIONIST", "DOCTOR", "LAB_TECHNICIAN", "PHARMACIST", "ACCOUNTANT"
            ));
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
        // Save clinic profile metadata
    }

    @FXML
    public void handleBackup() {
        String result = backupService.backupDatabase();
        if (backupStatusLabel != null) {
            backupStatusLabel.setText("Backup Saved: " + result);
        }
    }
}
