package com.clinical.ui.controller;

import com.clinical.security.entity.UserEntity;
import com.clinical.security.service.AuthService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MainClinicalController {

    private final ApplicationContext applicationContext;
    private final AuthService authService;
    private final com.clinical.admin.service.ClinicSettingService clinicSettingService;

    @FXML private StackPane mainContentArea;
    @FXML private Label userRoleLabel;
    @FXML private javafx.scene.image.ImageView clinicLogoImageView;
    @FXML private Label clinicNameLabel;

    @FXML private Button dashboardBtn;
    @FXML private Button patientBtn;
    @FXML private Button doctorRosterBtn;
    @FXML private Button visitBtn;
    @FXML private Button doctorBtn;
    @FXML private Button labBtn;
    @FXML private Button pharmacyBtn;
    @FXML private Button billingBtn;
    @FXML private Button reportsBtn;
    @FXML private Button adminBtn;

    public MainClinicalController(ApplicationContext applicationContext, AuthService authService, com.clinical.admin.service.ClinicSettingService clinicSettingService) {
        this.applicationContext = applicationContext;
        this.authService = authService;
        this.clinicSettingService = clinicSettingService;
    }

    @FXML
    public void initialize() {
        updateClinicBranding();
        showLoginView();
    }

    public void updateClinicBranding() {
        if (clinicNameLabel != null) {
            clinicNameLabel.setText(clinicSettingService.getSetting("CLINIC_NAME", "Apex Multispecialty Clinic"));
        }
        if (clinicLogoImageView != null) {
            String path = clinicSettingService.getSetting("CLINIC_LOGO_PATH", "images/default_logo.png");
            try {
                javafx.scene.image.Image img;
                if (path.startsWith("images/")) {
                    img = new javafx.scene.image.Image(getClass().getResourceAsStream("/" + path));
                } else {
                    img = new javafx.scene.image.Image(new java.io.File(path).toURI().toString());
                }
                clinicLogoImageView.setImage(img);
            } catch (Exception e) {
                // Ignore image load error
            }
        }
    }

    @FXML
    public void handleSignOut() {
        authService.setCurrentUser(null);
        if (userRoleLabel != null) {
            userRoleLabel.setText("Not Logged In");
        }
        showLoginView();
    }

    public void showLoginView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login_view.fxml"));
            loader.setControllerFactory(applicationContext::getBean);
            Node loginView = loader.load();
            LoginViewController controller = loader.getController();
            controller.setOnLoginSuccess(this::applyRolePermissionsAndShowDefaultView);
            if (mainContentArea != null) {
                mainContentArea.getChildren().setAll(loginView);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load login view", e);
        }
    }

    private void applyRolePermissionsAndShowDefaultView() {
        UserEntity currentUser = authService.getCurrentUser();
        if (currentUser == null) return;

        if (userRoleLabel != null) {
            userRoleLabel.setText("User: " + currentUser.getFullName() + " (" + currentUser.getRole() + ")");
        }

        String role = currentUser.getRole();

        // Control sidebar button visibility based on user role
        if (dashboardBtn != null) dashboardBtn.setVisible(true);
        if (patientBtn != null) patientBtn.setVisible("ADMIN".equals(role) || "RECEPTIONIST".equals(role));
        if (doctorRosterBtn != null) doctorRosterBtn.setVisible("ADMIN".equals(role) || "RECEPTIONIST".equals(role));
        if (visitBtn != null) visitBtn.setVisible("ADMIN".equals(role) || "RECEPTIONIST".equals(role) || "DOCTOR".equals(role));
        if (doctorBtn != null) doctorBtn.setVisible("ADMIN".equals(role) || "DOCTOR".equals(role));
        if (labBtn != null) labBtn.setVisible("ADMIN".equals(role) || "LAB_TECHNICIAN".equals(role) || "DOCTOR".equals(role));
        if (pharmacyBtn != null) pharmacyBtn.setVisible("ADMIN".equals(role) || "PHARMACIST".equals(role));
        if (billingBtn != null) billingBtn.setVisible("ADMIN".equals(role) || "ACCOUNTANT".equals(role) || "RECEPTIONIST".equals(role));
        if (reportsBtn != null) reportsBtn.setVisible(true);
        if (adminBtn != null) adminBtn.setVisible("ADMIN".equals(role));

        // Route default view per role
        switch (role) {
            case "DOCTOR" -> showDoctorModule();
            case "LAB_TECHNICIAN" -> showLabModule();
            case "PHARMACIST" -> showPharmacyModule();
            case "ACCOUNTANT" -> showBillingModule();
            default -> showDashboardModule();
        }
    }

    @FXML public void showDashboardModule() { loadView("/fxml/dashboard_view.fxml"); }
    @FXML public void showPatientModule() { loadView("/fxml/patient_view.fxml"); }
    @FXML public void showDoctorRosterModule() { loadView("/fxml/doctor_registration_view.fxml"); }
    @FXML public void showVisitModule() { loadView("/fxml/visit_view.fxml"); }
    @FXML public void showDoctorModule() { loadView("/fxml/doctor_view.fxml"); }
    @FXML public void showLabModule() { loadView("/fxml/lab_view.fxml"); }
    @FXML public void showPharmacyModule() { loadView("/fxml/pharmacy_view.fxml"); }
    @FXML public void showBillingModule() { loadView("/fxml/billing_view.fxml"); }
    @FXML public void showReportsModule() { loadView("/fxml/reports_view.fxml"); }
    @FXML public void showAdminModule() { loadView("/fxml/admin_view.fxml"); }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(applicationContext::getBean);
            Node view = loader.load();
            if (mainContentArea != null) {
                mainContentArea.getChildren().setAll(view);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load view: " + fxmlPath, e);
        }
    }
}

