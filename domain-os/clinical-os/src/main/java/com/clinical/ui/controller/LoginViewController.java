package com.clinical.ui.controller;

import com.clinical.security.entity.UserEntity;
import com.clinical.security.service.AuthService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class LoginViewController {

    private final AuthService authService;
    private Runnable onLoginSuccess;

    @FXML private ComboBox<String> roleCombo;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    public LoginViewController(AuthService authService) {
        this.authService = authService;
    }

    @FXML
    public void initialize() {
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
