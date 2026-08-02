package com.society.common.controller;

import com.society.app.StartupCoordinator;
import com.society.common.navigation.NavigationManager;
import com.society.common.navigation.NavigationState;
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


    private final StartupCoordinator startupCoordinator;

    private final SocietyContext societyContext;

    private final NavigationState navigationState;

    private static final String ACTIVE_STYLE = "navigation-button-active";

    public MainController(
            NavigationManager navigationManager,
            StartupCoordinator startupCoordinator,
            SocietyContext societyContext,
            NavigationState navigationState) {

        super(navigationManager);

        this.startupCoordinator = startupCoordinator;
        this.societyContext = societyContext;
        this.navigationState = navigationState;
    }

    @FXML
    public void initialize() {

        navigationManager.setContentHost(contentHost);
        startupCoordinator.start();

        updateNavigation();

        //membersButton.setDisable(true);
        sharesButton.setDisable(true);
        certificatesButton.setDisable(true);
        reportsButton.setDisable(true);
        administrationButton.setDisable(true);
        //navigationManager.navigate(View.DASHBOARD);

    }

    @FXML
    private void openDashboard() {
        navigationManager.navigate(View.DASHBOARD);
        updateNavigation();
    }

    @FXML
    private void openMembers() {
        navigationManager.navigate(View.MEMBERS);
        updateNavigation();
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

    private void updateNavigation() {

        dashboardButton.getStyleClass().remove(ACTIVE_STYLE);
        membersButton.getStyleClass().remove(ACTIVE_STYLE);
        sharesButton.getStyleClass().remove(ACTIVE_STYLE);
        certificatesButton.getStyleClass().remove(ACTIVE_STYLE);
        reportsButton.getStyleClass().remove(ACTIVE_STYLE);
        administrationButton.getStyleClass().remove(ACTIVE_STYLE);

        switch (navigationState.getCurrentView()) {

            case DASHBOARD -> dashboardButton.getStyleClass().add(ACTIVE_STYLE);

            case MEMBERS -> membersButton.getStyleClass().add(ACTIVE_STYLE);

            case SHARES -> sharesButton.getStyleClass().add(ACTIVE_STYLE);

            case CERTIFICATES -> certificatesButton.getStyleClass().add(ACTIVE_STYLE);

            case REPORTS -> reportsButton.getStyleClass().add(ACTIVE_STYLE);

            case ADMINISTRATION -> administrationButton.getStyleClass().add(ACTIVE_STYLE);

            default -> {
            }
        }
    }


}