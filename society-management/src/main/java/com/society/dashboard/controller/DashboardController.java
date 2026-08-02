package com.society.dashboard.controller;

import com.society.common.controller.BaseController;
import com.society.common.navigation.NavigationManager;
import com.society.dashboard.dto.DashboardSummaryDto;
import com.society.dashboard.service.DashboardService;
import com.society.society.runtime.SocietyProvider;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.springframework.stereotype.Component;

@Component
public class DashboardController extends BaseController {

    private final DashboardService dashboardService;

    private final SocietyProvider societyProvider;

    @FXML
    private Label membersCountLabel;

    @FXML
    private Label sharesCountLabel;

    @FXML
    private Label certificatesCountLabel;

    @FXML
    private Label databaseStatusLabel;

    @FXML
    private Label welcomeLabel;

    public DashboardController(
            NavigationManager navigationManager,
            DashboardService dashboardService, SocietyProvider societyProvider) {

        super(navigationManager);
        this.dashboardService = dashboardService;
        this.societyProvider = societyProvider;
    }

    @FXML
    public void initialize() {
        loadDashboard();
    }

    private void loadDashboard() {

        if (societyProvider.hasSociety()) {

            welcomeLabel.setText(
                    "Welcome to "
                            + societyProvider.getCurrentSociety().name());

        } else {

            welcomeLabel.setText(
                    "Welcome to Society Management System");

        }

        DashboardSummaryDto summary = dashboardService.getSummary();

        membersCountLabel.setText(
                String.valueOf(summary.totalMembers()));

        sharesCountLabel.setText(
                String.valueOf(summary.totalShares()));

        certificatesCountLabel.setText(
                String.valueOf(summary.totalCertificates()));

        databaseStatusLabel.setText(
                summary.databaseConnected()
                        ? "Connected"
                        : "Disconnected");
    }

    @FXML
    public void refresh() {
        loadDashboard();
    }
}