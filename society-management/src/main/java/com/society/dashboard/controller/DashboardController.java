package com.society.dashboard.controller;

import com.society.common.controller.BaseController;
import com.society.common.navigation.NavigationManager;
import com.society.dashboard.dto.DashboardSummaryDto;
import com.society.dashboard.service.DashboardService;
import com.society.governance.entity.MeetingEntity;
import com.society.governance.service.GovernanceService;
import com.society.operations.entity.AssetEntity;
import com.society.operations.service.OperationsService;
import com.society.society.runtime.SocietyProvider;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DashboardController extends BaseController {

    private final DashboardService dashboardService;
    private final SocietyProvider societyProvider;
    private final OperationsService operationsService;
    private final GovernanceService governanceService;

    @FXML private Label membersCountLabel;
    @FXML private Label sharesCountLabel;
    @FXML private Label certificatesCountLabel;
    @FXML private Label databaseStatusLabel;
    @FXML private Label welcomeLabel;
    @FXML private ListView<String> notificationsListView;

    public DashboardController(
            NavigationManager navigationManager,
            DashboardService dashboardService,
            SocietyProvider societyProvider,
            OperationsService operationsService,
            GovernanceService governanceService) {

        super(navigationManager);
        this.dashboardService = dashboardService;
        this.societyProvider = societyProvider;
        this.operationsService = operationsService;
        this.governanceService = governanceService;
    }

    @FXML
    public void initialize() {
        loadDashboard();
    }

    private void loadDashboard() {

        if (societyProvider.hasSociety()) {
            welcomeLabel.setText("Welcome to " + societyProvider.getCurrentSociety().name());
        } else {
            welcomeLabel.setText("Welcome to Society Office OS");
        }

        DashboardSummaryDto summary = dashboardService.getSummary();

        membersCountLabel.setText(String.valueOf(summary.totalMembers()));
        sharesCountLabel.setText(String.valueOf(summary.totalShares()));
        certificatesCountLabel.setText(String.valueOf(summary.totalCertificates()));
        databaseStatusLabel.setText(summary.databaseConnected() ? "Connected" : "Disconnected");

        loadSystemNotifications();
    }

    private void loadSystemNotifications() {
        if (notificationsListView == null) return;
        notificationsListView.getItems().clear();
        List<String> alerts = new ArrayList<>();

        // 1. Upcoming General Body & Committee Meetings
        try {
            List<MeetingEntity> meetings = governanceService.getAllMeetings();
            for (MeetingEntity m : meetings) {
                if ("SCHEDULED".equalsIgnoreCase(m.getStatus())) {
                    alerts.add("📅 UPCOMING MEETING: " + m.getTitle() + " (" + m.getMeetingType() + ") scheduled for " + m.getMeetingDate() + " at " + m.getMeetingTime());
                }
            }
        } catch (Exception ignored) {}

        // 2. AMC Contract Expiry Alerts
        try {
            List<AssetEntity> assets = operationsService.getAllAssets();
            for (AssetEntity a : assets) {
                if (a.getAmcExpiryDate() != null && !a.getAmcExpiryDate().isBlank()) {
                    alerts.add("🛠️ AMC CONTRACT EXPIRY: " + a.getName() + " (" + a.getCategory() + ") - Vendor: " 
                            + (a.getAmcVendor() != null ? a.getAmcVendor().getName() : "Unassigned") + " | Expiry Date: " + a.getAmcExpiryDate());
                }
            }
        } catch (Exception ignored) {}

        // 3. Staff Police Verification Pending Alerts
        try {
            List<com.society.operations.entity.SocietyStaffEntity> staff = operationsService.getAllStaff();
            for (com.society.operations.entity.SocietyStaffEntity s : staff) {
                if ("PENDING".equalsIgnoreCase(s.getPoliceVerificationStatus()) && s.isActive()) {
                    alerts.add("⚠️ POLICE VERIFICATION PENDING: On-Premise Staff '" + s.getName() + "' (" + s.getRole() + ") requires verification submission.");
                }
            }
        } catch (Exception ignored) {}

        if (alerts.isEmpty()) {
            alerts.add("✅ System Normal: No urgent AMC expirations, pending meetings, or verification alerts requiring immediate action.");
        }

        notificationsListView.setItems(FXCollections.observableArrayList(alerts));
    }

    @FXML
    public void refresh() {
        loadDashboard();
    }
}