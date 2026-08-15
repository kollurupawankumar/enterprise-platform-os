package com.clinical.ui.controller;

import com.clinical.admin.service.ClinicSettingService;
import com.clinical.security.entity.UserEntity;
import com.clinical.security.service.AuthService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Optional;

@Component
public class LoginViewController {

    private final AuthService authService;
    private final ClinicSettingService clinicSettingService;
    private Runnable onLoginSuccess;

    @FXML private ImageView clinicLogoImageView;
    @FXML private Label clinicNameLabel;
    @FXML private ComboBox<String> roleCombo;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    public LoginViewController(AuthService authService, ClinicSettingService clinicSettingService) {
        this.authService = authService;
        this.clinicSettingService = clinicSettingService;
    }

    @FXML
    public void initialize() {
        loadClinicBranding();

        if (roleCombo != null) {
            roleCombo.setItems(FXCollections.observableArrayList(
                    "ADMIN", "RECEPTIONIST", "DOCTOR", "LAB_TECHNICIAN", "PHARMACIST", "ACCOUNTANT"
            ));
            roleCombo.getSelectionModel().select("ADMIN");
            roleCombo.setOnAction(e -> {
                String role = roleCombo.getValue();
                if (usernameField != null) {
                    usernameField.setText(role.toLowerCase());
                    passwordField.setText("password123");
                }
            });
            usernameField.setText("admin");
            passwordField.setText("password123");
        }
    }

    private void loadClinicBranding() {
        if (clinicNameLabel != null) {
            clinicNameLabel.setText(clinicSettingService.getSetting("CLINIC_NAME", "Apex Multispecialty Clinic"));
        }
        if (clinicLogoImageView != null) {
            String path = clinicSettingService.getSetting("CLINIC_LOGO_PATH", "images/default_logo.png");
            try {
                Image img;
                if (path.startsWith("images/")) {
                    img = new Image(getClass().getResourceAsStream("/" + path));
                } else {
                    img = new Image(new File(path).toURI().toString());
                }
                clinicLogoImageView.setImage(img);
            } catch (Exception ignored) {}
        }
    }

    public void setOnLoginSuccess(Runnable onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;
    }

    @FXML
    public void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        Optional<UserEntity> userOpt = authService.authenticate(username, password);
        if (userOpt.isPresent()) {
            if (errorLabel != null) errorLabel.setVisible(false);
            if (onLoginSuccess != null) {
                onLoginSuccess.run();
            }
        } else {
            if (errorLabel != null) {
                errorLabel.setText("Invalid credentials or role mismatch");
                errorLabel.setVisible(true);
            }
        }
    }
}
