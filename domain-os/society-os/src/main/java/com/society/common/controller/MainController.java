package com.society.common.controller;

import com.society.ai.controller.AiCopilotController;
import com.society.app.StartupCoordinator;
import com.society.common.navigation.NavigationManager;
import com.society.common.navigation.NavigationState;
import com.society.common.navigation.View;
import com.society.society.context.SocietyContext;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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

    @FXML
    private Button aiCopilotButton;

    @FXML
    private VBox aiCopilotInclude;

    @FXML
    private AiCopilotController aiCopilotIncludeController;

    private final StartupCoordinator startupCoordinator;
    private final SocietyContext societyContext;
    private final NavigationState navigationState;
    private final com.society.user.context.UserContext userContext;

    private javafx.scene.Node initialTop;
    private javafx.scene.Node initialLeft;
    private javafx.scene.Node initialBottom;
    private javafx.scene.Node initialRight;

    private static final String ACTIVE_STYLE = "navigation-button-active";

    @FXML private com.society.header.controller.HeaderController headerIncludeController;

    public MainController(
            NavigationManager navigationManager,
            StartupCoordinator startupCoordinator,
            SocietyContext societyContext,
            NavigationState navigationState,
            com.society.user.context.UserContext userContext) {

        super(navigationManager);

        this.startupCoordinator = startupCoordinator;
        this.societyContext = societyContext;
        this.navigationState = navigationState;
        this.userContext = userContext;
    }

    @FXML
    public void initialize() {

        initialTop = rootBorderPane.getTop();
        initialLeft = rootBorderPane.getLeft();
        initialBottom = rootBorderPane.getBottom();
        initialRight = rootBorderPane.getRight();

        // Default: Copilot drawer is hidden until user clicks "🤖 AI Copilot" button
        rootBorderPane.setRight(null);

        if (aiCopilotIncludeController != null) {
            aiCopilotIncludeController.setOnCloseHandler(() -> rootBorderPane.setRight(null));
        }

        navigationManager.setContentHost(contentHost);

        navigationState.addListener(view -> {
            if (view == View.LOGIN || view == View.SOCIETY_SETUP) {
                rootBorderPane.setTop(null);
                rootBorderPane.setLeft(null);
                rootBorderPane.setBottom(null);
                rootBorderPane.setRight(null);
            } else {
                rootBorderPane.setTop(initialTop);
                rootBorderPane.setLeft(initialLeft);
                rootBorderPane.setBottom(initialBottom);
                if (headerIncludeController != null) {
                    headerIncludeController.refresh();
                }
                updateNavigation();
            }
        });

        startupCoordinator.start();
    }

    @FXML
    private void toggleAiCopilot() {
        if (rootBorderPane.getRight() == null) {
            rootBorderPane.setRight(initialRight);
        } else {
            rootBorderPane.setRight(null);
        }
    }

    private void navigateAuthorized(View view, String module) {
        if (userContext.canAccess(module)) {
            navigationManager.navigate(view);
            updateNavigation();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Access Denied");
            alert.setHeaderText("Permission Required");
            alert.setContentText("Your role (" + (userContext.getCurrentUser() != null ? userContext.getCurrentUser().getRole() : "Guest") + ") is not authorized to access the " + module + " module.");
            alert.showAndWait();
        }
    }

    @FXML
    private void openDashboard() {
        navigateAuthorized(View.DASHBOARD, "DASHBOARD");
    }

    @FXML
    private void openMembers() {
        navigateAuthorized(View.MEMBERS, "MEMBERS");
    }

    @FXML
    private void openProperties() {
        navigateAuthorized(View.PROPERTIES, "PROPERTIES");
    }

    @FXML
    private void openGovernance() {
        navigateAuthorized(View.GOVERNANCE, "GOVERNANCE");
    }

    @FXML
    private void openOperations() {
        navigateAuthorized(View.OPERATIONS, "OPERATIONS");
    }

    @FXML
    private void openFinance() {
        navigateAuthorized(View.FINANCE, "FINANCE");
    }

    @FXML
    private void openSearch() {
        navigateAuthorized(View.SEARCH, "SEARCH");
    }

    @FXML
    private void openShares() {
        navigateAuthorized(View.SHARES, "SHARES");
    }

    @FXML
    private void openCertificates() {
        navigateAuthorized(View.CERTIFICATES, "CERTIFICATES");
    }

    @FXML
    private void openReports() {
        navigateAuthorized(View.REPORTS, "REPORTS");
    }

    @FXML
    private void openAdministration() {
        navigateAuthorized(View.ADMINISTRATION, "ADMINISTRATION");
    }

    @FXML
    private void openDocuments() {
        navigateAuthorized(View.DOCUMENTS, "DOCUMENTS");
    }

    @FXML
    private void openKnowledgeBase() {
        navigateAuthorized(View.KNOWLEDGE_BASE, "KNOWLEDGE_BASE");
    }

    @FXML
    private void openBudgetAudit() {
        navigateAuthorized(View.BUDGET_AUDIT, "BUDGET_AUDIT");
    }

    private void updateNavigation() {
        // Role-based Sidebar Button Visibility & Managed Status
        setButtonVisibility(dashboardButton, "DASHBOARD");
        setButtonVisibility(membersButton, "MEMBERS");
        setButtonVisibility(propertiesButton, "PROPERTIES");
        setButtonVisibility(governanceButton, "GOVERNANCE");
        setButtonVisibility(operationsButton, "OPERATIONS");
        setButtonVisibility(financeButton, "FINANCE");
        setButtonVisibility(searchButton, "SEARCH");
        setButtonVisibility(sharesButton, "SHARES");
        setButtonVisibility(certificatesButton, "CERTIFICATES");
        setButtonVisibility(reportsButton, "REPORTS");
        setButtonVisibility(documentsButton, "DOCUMENTS");
        setButtonVisibility(knowledgeBaseButton, "KNOWLEDGE_BASE");
        setButtonVisibility(budgetAuditButton, "BUDGET_AUDIT");
        setButtonVisibility(administrationButton, "ADMINISTRATION");

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

    private void setButtonVisibility(Button btn, String module) {
        if (btn == null) return;
        boolean allowed = userContext.canAccess(module);
        btn.setVisible(allowed);
        btn.setManaged(allowed);
    }
}