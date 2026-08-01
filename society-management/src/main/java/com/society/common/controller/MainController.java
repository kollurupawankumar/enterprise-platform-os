package com.society.common.controller;

import com.society.app.StartupCoordinator;
import com.society.common.navigation.NavigationManager;
import com.society.common.navigation.View;
import com.society.society.context.SocietyContext;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.springframework.stereotype.Component;

@Component
public class MainController extends BaseController {

    @FXML
    private StackPane contentHost;

    @FXML
    private Button dashboardButton;

    @FXML
    private Button membersButton;

    @FXML
    private Button sharesButton;

    @FXML
    private Button certificatesButton;

    @FXML
    private Button reportsButton;

    @FXML
    private Button administrationButton;

    @FXML
    private Label societyNameLabel;

    private final StartupCoordinator startupCoordinator;

    private final SocietyContext societyContext;

    public MainController(NavigationManager navigationManager,
                          StartupCoordinator startupCoordinator,
                          SocietyContext societyContext) {
        super(navigationManager);
        this.startupCoordinator = startupCoordinator;
        this.societyContext = societyContext;
    }

    @FXML
    public void initialize() {

        navigationManager.setContentHost(contentHost);
        startupCoordinator.start();
        refreshHeader();
        //navigationManager.navigate(View.DASHBOARD);

    }

    @FXML
    private void openDashboard() {
        navigationManager.navigate(View.DASHBOARD);
    }

    @FXML
    private void openMembers() {
        showNotImplemented("Members");
    }

    @FXML
    private void openShares() {
        showNotImplemented("Shares");
    }

    @FXML
    private void openCertificates() {
        showNotImplemented("Certificates");
    }

    @FXML
    private void openReports() {
        showNotImplemented("Reports");
    }

    @FXML
    private void openAdministration() {
        showNotImplemented("Administration");
    }

    private void showNotImplemented(String module) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Module");
        alert.setHeaderText(null);
        alert.setContentText(module + " module will be available in a future commit.");
        alert.showAndWait();
    }

    private void refreshHeader() {

        if (societyContext.hasSociety()) {

            societyNameLabel.setText(
                    societyContext.getCurrentSociety().name());

        } else {

            societyNameLabel.setText(
                    "Society Management System");

        }

    }

}