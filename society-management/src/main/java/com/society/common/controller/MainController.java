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
    private BorderPane rootBorderPane;

    @FXML
    private StackPane contentHost;

    @FXML
    private Button dashboardButton;

    @FXML
    private Button membersButton;

    @FXML
    private Button propertiesButton;

    @FXML
    private Button governanceButton;

    @FXML
    private Button operationsButton;

    @FXML
    private Button financeButton;

    @FXML
    private Button searchButton;

    @FXML
    private Button sharesButton;

    @FXML
    private Button certificatesButton;

    @FXML
    private Button reportsButton;

    @FXML
    private Button administrationButton;

    @FXML
    private Button documentsButton;

    @FXML
    private Button knowledgeBaseButton;

    @FXML
    private Button budgetAuditButton;


    private final StartupCoordinator startupCoordinator;

    private final SocietyContext societyContext;

    private final NavigationState navigationState;

    private javafx.scene.Node initialTop;
    private javafx.scene.Node initialLeft;
    private javafx.scene.Node initialBottom;

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

        initialTop = rootBorderPane.getTop();
        initialLeft = rootBorderPane.getLeft();
        initialBottom = rootBorderPane.getBottom();

        navigationManager.setContentHost(contentHost);

        navigationState.addListener(view -> {
            if (view == View.LOGIN || view == View.SOCIETY_SETUP) {
                rootBorderPane.setTop(null);
                rootBorderPane.setLeft(null);
                rootBorderPane.setBottom(null);
            } else {
                rootBorderPane.setTop(initialTop);
                rootBorderPane.setLeft(initialLeft);
                rootBorderPane.setBottom(initialBottom);
                updateNavigation();
            }
        });

        startupCoordinator.start();
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
    private void openProperties() {
        navigationManager.navigate(View.PROPERTIES);
        updateNavigation();
    }

    @FXML
    private void openGovernance() {
        navigationManager.navigate(View.GOVERNANCE);
        updateNavigation();
    }

    @FXML
    private void openOperations() {
        navigationManager.navigate(View.OPERATIONS);
        updateNavigation();
    }

    @FXML
    private void openFinance() {
        navigationManager.navigate(View.FINANCE);
        updateNavigation();
    }

    @FXML
    private void openSearch() {
        navigationManager.navigate(View.SEARCH);
        updateNavigation();
    }

    @FXML
    private void openShares() {
        navigationManager.navigate(View.SHARES);
        updateNavigation();
    }

    @FXML
    private void openCertificates() {
        navigationManager.navigate(View.CERTIFICATES);
        updateNavigation();
    }

    @FXML
    private void openReports() {
        navigationManager.navigate(View.REPORTS);
        updateNavigation();
    }

    @FXML
    private void openAdministration() {
        navigationManager.navigate(View.ADMINISTRATION);
        updateNavigation();
    }

    @FXML
    private void openDocuments() {
        navigationManager.navigate(View.DOCUMENTS);
        updateNavigation();
    }

    @FXML
    private void openKnowledgeBase() {
        navigationManager.navigate(View.KNOWLEDGE_BASE);
        updateNavigation();
    }

    @FXML
    private void openBudgetAudit() {
        navigationManager.navigate(View.BUDGET_AUDIT);
        updateNavigation();
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
        propertiesButton.getStyleClass().remove(ACTIVE_STYLE);
        governanceButton.getStyleClass().remove(ACTIVE_STYLE);
        operationsButton.getStyleClass().remove(ACTIVE_STYLE);
        financeButton.getStyleClass().remove(ACTIVE_STYLE);
        searchButton.getStyleClass().remove(ACTIVE_STYLE);
        sharesButton.getStyleClass().remove(ACTIVE_STYLE);
        certificatesButton.getStyleClass().remove(ACTIVE_STYLE);
        reportsButton.getStyleClass().remove(ACTIVE_STYLE);
        administrationButton.getStyleClass().remove(ACTIVE_STYLE);
        if (documentsButton != null) documentsButton.getStyleClass().remove(ACTIVE_STYLE);
        if (knowledgeBaseButton != null) knowledgeBaseButton.getStyleClass().remove(ACTIVE_STYLE);
        if (budgetAuditButton != null) budgetAuditButton.getStyleClass().remove(ACTIVE_STYLE);

        switch (navigationState.getCurrentView()) {

            case DASHBOARD -> dashboardButton.getStyleClass().add(ACTIVE_STYLE);

            case MEMBERS -> membersButton.getStyleClass().add(ACTIVE_STYLE);

            case PROPERTIES -> propertiesButton.getStyleClass().add(ACTIVE_STYLE);

            case GOVERNANCE -> governanceButton.getStyleClass().add(ACTIVE_STYLE);

            case OPERATIONS -> operationsButton.getStyleClass().add(ACTIVE_STYLE);

            case FINANCE -> financeButton.getStyleClass().add(ACTIVE_STYLE);

            case SEARCH -> searchButton.getStyleClass().add(ACTIVE_STYLE);

            case SHARES -> sharesButton.getStyleClass().add(ACTIVE_STYLE);

            case CERTIFICATES -> certificatesButton.getStyleClass().add(ACTIVE_STYLE);

            case REPORTS -> reportsButton.getStyleClass().add(ACTIVE_STYLE);

            case ADMINISTRATION -> administrationButton.getStyleClass().add(ACTIVE_STYLE);

            case DOCUMENTS -> {
                if (documentsButton != null) documentsButton.getStyleClass().add(ACTIVE_STYLE);
            }

            case KNOWLEDGE_BASE -> {
                if (knowledgeBaseButton != null) knowledgeBaseButton.getStyleClass().add(ACTIVE_STYLE);
            }

            case BUDGET_AUDIT -> {
                if (budgetAuditButton != null) budgetAuditButton.getStyleClass().add(ACTIVE_STYLE);
            }

            default -> {
            }
        }
    }


}