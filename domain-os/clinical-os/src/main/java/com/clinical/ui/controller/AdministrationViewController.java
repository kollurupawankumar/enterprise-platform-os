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
        if (clinicNameField != null) {
            clinicNameField.setText(clinicSettingService.getSetting("CLINIC_NAME", "Apex Multispecialty Clinic"));
        }
        if (logoPathField != null) {
            String logoPath = clinicSettingService.getSetting("CLINIC_LOGO_PATH", "images/default_logo.png");
            logoPathField.setText(logoPath);
            loadLogoPreview(logoPath);
        }
        if (idPrefixField != null) {
            idPrefixField.setText(clinicSettingService.getSetting("PATIENT_ID_PREFIX", "PAT"));
        }
        if (idFormatField != null) {
            idFormatField.setText(clinicSettingService.getSetting("PATIENT_ID_FORMAT", "PAT-{YYYY}-{SEQ}"));
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
        if (idPrefixField != null && !idPrefixField.getText().isEmpty()) {
            clinicSettingService.saveSetting("PATIENT_ID_PREFIX", idPrefixField.getText());
        }
        if (idFormatField != null && !idFormatField.getText().isEmpty()) {
            clinicSettingService.saveSetting("PATIENT_ID_FORMAT", idFormatField.getText());
        }
        if (clinicNameField != null) {
            clinicSettingService.saveSetting("CLINIC_NAME", clinicNameField.getText());
        }
        if (logoPathField != null) {
            clinicSettingService.saveSetting("CLINIC_LOGO_PATH", logoPathField.getText());
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
