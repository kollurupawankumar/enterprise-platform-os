package com.society.common.controller;

import com.society.common.navigation.NavigationManager;
import com.society.common.navigation.View;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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

    public MainController(NavigationManager navigationManager) {
        super(navigationManager);
    }

    @FXML
    public void initialize() {

        navigationManager.setContentHost(contentHost);

        navigationManager.navigate(View.DASHBOARD);

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

}