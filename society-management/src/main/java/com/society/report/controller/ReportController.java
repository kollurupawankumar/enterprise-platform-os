package com.society.report.controller;

import com.society.common.controller.BaseController;
import com.society.common.navigation.NavigationManager;
import com.society.finance.entity.ExpenseEntity;
import com.society.finance.repository.ExpenseRepository;
import com.society.governance.entity.ManagingCommitteeEntity;
import com.society.governance.entity.MeetingEntity;
import com.society.governance.service.GovernanceService;
import com.society.member.dto.MemberDto;
import com.society.member.service.MemberService;
import com.society.operations.entity.AssetEntity;
import com.society.operations.entity.SocietyStaffEntity;
import com.society.operations.service.OperationsService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ReportController extends BaseController {

    private final MemberService memberService;
    private final GovernanceService governanceService;
    private final OperationsService operationsService;
    private final ExpenseRepository expenseRepository;

    @FXML private ComboBox<String> reportTypeCombo;
    @FXML private Label reportDescriptionLabel;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private RadioButton pdfRadio;
    @FXML private RadioButton excelRadio;
    @FXML private TableView<ExportLog> exportsTable;
    @FXML private TableColumn<ExportLog, String> nameCol;
    @FXML private TableColumn<ExportLog, String> formatCol;
    @FXML private TableColumn<ExportLog, String> dateCol;

    private final ObservableList<ExportLog> exportLogs = FXCollections.observableArrayList();
    private final Map<String, String> reportDescriptions = new HashMap<>();

    public ReportController(
            NavigationManager navigationManager,
            MemberService memberService,
            GovernanceService governanceService,
            OperationsService operationsService,
            ExpenseRepository expenseRepository) {
        super(navigationManager);
        this.memberService = memberService;
        this.governanceService = governanceService;
        this.operationsService = operationsService;
        this.expenseRepository = expenseRepository;
    }

    @FXML
    public void initialize() {
        setupReportDescriptions();

        reportTypeCombo.setItems(FXCollections.observableArrayList(
                "Form I - Register of Members (Rule 32)",
                "Form J - Register of Shares (Rule 33)",
                "Form M - Managing Committee Office Bearers List",
                "AGM & General Body Meeting Attendance Summary",
                "Financial Expense Voucher & Ledger Summary",
                "Asset Register & AMC Contract Expiry Schedule",
                "On-Premise Staff & Police Verification Registry"
        ));

        reportTypeCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                reportDescriptionLabel.setText(reportDescriptions.getOrDefault(newVal, "Statutory Housing Society Report."));
            }
        });

        reportTypeCombo.getSelectionModel().selectFirst();

        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().name));
        formatCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().format));
        dateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().timestamp));

        exportsTable.setItems(exportLogs);
    }

    private void setupReportDescriptions() {
        reportDescriptions.put("Form I - Register of Members (Rule 32)",
                "📜 Statutory Register under Rule 32 of Co-operative Housing Society Act. Documents member names, admission dates, flat numbers, share certificates, and nominee details.");
        reportDescriptions.put("Form J - Register of Shares (Rule 33)",
                "📜 Statutory Share Capital Register under Rule 33. Tracks share allotment range, certificate numbers, face value, and member transfer history.");
        reportDescriptions.put("Form M - Managing Committee Office Bearers List",
                "🏛️ Statutory Annual Submission for Deputy Registrar. Lists active Office Bearers (President, Vice-President, Secretary, Treasurer) and committee members.");
        reportDescriptions.put("AGM & General Body Meeting Attendance Summary",
                "📅 Governance & Meeting Log. Lists scheduled AGM/SGM meetings, attendance lists, quorum, and recorded resolutions.");
        reportDescriptions.put("Financial Expense Voucher & Ledger Summary",
                "💰 Financial Audit Ledger. Itemizes society expenses, voucher numbers, payment modes, payees, and debit bank accounts.");
        reportDescriptions.put("Asset Register & AMC Contract Expiry Schedule",
                "🛠️ Operations Register. Lists society assets (lifts, generators, pumps), warranty expiry dates, vendor contacts, and AMC contract schedules.");
        reportDescriptions.put("On-Premise Staff & Police Verification Registry",
                "👮 Security & Compliance Register. Lists society guards, housekeeping personnel, shift timings, mobile numbers, and Police Verification status.");
    }

    @FXML
    private void handleGenerateReport() {
        String reportName = reportTypeCombo.getValue();
        boolean isPdf = pdfRadio.isSelected();
        String ext = isPdf ? "pdf" : "csv";
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save " + reportName);
        String safeName = reportName.replaceAll("[^a-zA-Z0-9]", "_");
        chooser.setInitialFileName(safeName + "." + ext);
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(isPdf ? "PDF Document (*.pdf)" : "CSV File (*.csv)", "*." + ext));

        File targetFile = chooser.showSaveDialog(reportTypeCombo.getScene().getWindow());
        if (targetFile != null) {
            try {
                String content = buildReportContent(reportName, isPdf);
                Files.writeString(targetFile.toPath(), content);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Report Generated Successfully");
                alert.setHeaderText(reportName);
                alert.setContentText("Report exported to:\n" + targetFile.getAbsolutePath());
                alert.showAndWait();

                exportLogs.add(0, new ExportLog(reportName, isPdf ? "PDF" : "CSV/Excel", time));
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Export Failed");
                alert.setContentText("Could not generate report: " + ex.getMessage());
                alert.showAndWait();
            }
        }
    }

    private String buildReportContent(String reportType, boolean isPdf) {
        StringBuilder sb = new StringBuilder();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

        if (!isPdf) {
            // CSV Format
            if (reportType.startsWith("Form I")) {
                sb.append("Member No,Full Name,Member Type,Status,Mobile Number,Email,Admission Date,Resolution No\n");
                for (MemberDto m : memberService.findAll()) {
                    sb.append(cleanCsv(m.memberNumber())).append(",")
                      .append(cleanCsv(m.firstName() + " " + m.lastName())).append(",")
                      .append(cleanCsv(m.memberType())).append(",")
                      .append(cleanCsv(m.status() != null ? m.status().name() : "")).append(",")
                      .append(cleanCsv(m.mobileNumber())).append(",")
                      .append(cleanCsv(m.email())).append(",")
                      .append(cleanCsv(m.admissionDate())).append(",")
                      .append(cleanCsv(m.resolutionNumber())).append("\n");
                }
            } else if (reportType.startsWith("Financial")) {
                sb.append("Voucher No,Date,Payee,Category,Amount (INR),Payment Mode,Remarks\n");
                for (ExpenseEntity e : expenseRepository.findAll()) {
                    sb.append(cleanCsv(e.getVoucherNumber())).append(",")
                      .append(cleanCsv(e.getExpenseDate())).append(",")
                      .append(cleanCsv(e.getPayee())).append(",")
                      .append(cleanCsv(e.getCategory())).append(",")
                      .append(e.getAmount() != null ? e.getAmount() : 0.0).append(",")
                      .append(cleanCsv(e.getPaymentMode())).append(",")
                      .append(cleanCsv(e.getRemarks())).append("\n");
                }
            } else if (reportType.startsWith("Asset")) {
                sb.append("Asset Name,Category,Vendor,Serial No,Purchase Cost,AMC Start Date,AMC Expiry Date\n");
                for (AssetEntity a : operationsService.getAllAssets()) {
                    sb.append(cleanCsv(a.getName())).append(",")
                      .append(cleanCsv(a.getCategory())).append(",")
                      .append(cleanCsv(a.getAmcVendor() != null ? a.getAmcVendor().getName() : "Unassigned")).append(",")
                      .append(cleanCsv(a.getSerialNumber())).append(",")
                      .append(a.getPurchaseCost() != null ? a.getPurchaseCost() : 0.0).append(",")
                      .append(cleanCsv(a.getAmcStartDate())).append(",")
                      .append(cleanCsv(a.getAmcExpiryDate())).append("\n");
                }
            } else if (reportType.startsWith("On-Premise")) {
                sb.append("Staff Name,Role,Mobile Number,Shift Timing,Police Verification Status,Active Status\n");
                for (SocietyStaffEntity s : operationsService.getAllStaff()) {
                    sb.append(cleanCsv(s.getName())).append(",")
                      .append(cleanCsv(s.getRole())).append(",")
                      .append(cleanCsv(s.getMobileNumber())).append(",")
                      .append(cleanCsv(s.getShiftTiming())).append(",")
                      .append(cleanCsv(s.getPoliceVerificationStatus())).append(",")
                      .append(s.isActive() ? "ACTIVE" : "INACTIVE").append("\n");
                }
            } else {
                sb.append("Title,Type,Status,Date,Time\n");
                for (MeetingEntity m : governanceService.getAllMeetings()) {
                    sb.append(cleanCsv(m.getTitle())).append(",")
                      .append(cleanCsv(m.getMeetingType())).append(",")
                      .append(cleanCsv(m.getStatus())).append(",")
                      .append(cleanCsv(m.getMeetingDate())).append(",")
                      .append(cleanCsv(m.getMeetingTime())).append("\n");
                }
            }
            return sb.toString();
        }

        // Formatted Document Summary
        sb.append("=========================================================================================\n");
        sb.append("                       SOCIETY OFFICE OS - ").append(reportType.toUpperCase()).append("\n");
        sb.append("=========================================================================================\n");
        sb.append("Generated On: ").append(timestamp).append("\n");
        sb.append("Statutory Compliance: Rule 32 / Rule 33 Co-operative Housing Society Bye-Laws\n");
        sb.append("-----------------------------------------------------------------------------------------\n\n");

        if (reportType.startsWith("Form I")) {
            for (MemberDto m : memberService.findAll()) {
                sb.append("Member No   : ").append(m.memberNumber()).append(" | Full Name: ").append(m.firstName()).append(" ").append(m.lastName()).append("\n");
                sb.append("Type        : ").append(m.memberType()).append(" | Status: ").append(m.status()).append("\n");
                sb.append("Mobile      : ").append(m.mobileNumber()).append(" | Email: ").append(m.email()).append("\n");
                sb.append("Admission   : ").append(m.admissionDate()).append(" | Resolution No: ").append(m.resolutionNumber()).append("\n");
                sb.append("-----------------------------------------------------------------------------------------\n");
            }
        } else if (reportType.startsWith("Financial")) {
            for (ExpenseEntity e : expenseRepository.findAll()) {
                sb.append("Voucher No  : ").append(e.getVoucherNumber()).append(" | Date: ").append(e.getExpenseDate()).append("\n");
                sb.append("Payee       : ").append(e.getPayee()).append(" | Category: ").append(e.getCategory()).append("\n");
                sb.append("Amount      : ").append(com.society.common.util.CurrencyUtils.formatInr(e.getAmount())).append(" | Mode: ").append(e.getPaymentMode()).append("\n");
                sb.append("Narration   : ").append(e.getRemarks() != null ? e.getRemarks() : "N/A").append("\n");
                sb.append("-----------------------------------------------------------------------------------------\n");
            }
        } else if (reportType.startsWith("Form M")) {
            for (ManagingCommitteeEntity mc : governanceService.getAllCommitteeMembers()) {
                sb.append("Office Bearer : ").append(mc.getDesignation()).append("\n");
                sb.append("Member        : ").append(mc.getMember() != null ? mc.getMember().getFirstName() + " " + mc.getMember().getLastName() : "-")
                  .append(" | Term: ").append(mc.getStartDate()).append(" to ").append(mc.getEndDate() != null ? mc.getEndDate() : "Present").append("\n");
                sb.append("Status        : ").append(mc.getStatus()).append("\n");
                sb.append("-----------------------------------------------------------------------------------------\n");
            }
        } else if (reportType.startsWith("Asset")) {
            for (AssetEntity a : operationsService.getAllAssets()) {
                sb.append("Asset Name  : ").append(a.getName()).append(" | Category: ").append(a.getCategory()).append("\n");
                sb.append("AMC Vendor  : ").append(a.getAmcVendor() != null ? a.getAmcVendor().getName() : "Unassigned").append("\n");
                sb.append("AMC Start   : ").append(a.getAmcStartDate()).append(" | Expiry Date: ").append(a.getAmcExpiryDate()).append("\n");
                sb.append("-----------------------------------------------------------------------------------------\n");
            }
        } else if (reportType.startsWith("On-Premise")) {
            for (SocietyStaffEntity s : operationsService.getAllStaff()) {
                sb.append("Staff Name   : ").append(s.getName()).append(" | Role: ").append(s.getRole()).append("\n");
                sb.append("Mobile       : ").append(s.getMobileNumber()).append(" | Shift: ").append(s.getShiftTiming()).append("\n");
                sb.append("Verification : ").append(s.getPoliceVerificationStatus()).append(" | Status: ").append(s.isActive() ? "ACTIVE" : "INACTIVE").append("\n");
                sb.append("-----------------------------------------------------------------------------------------\n");
            }
        } else {
            for (MeetingEntity m : governanceService.getAllMeetings()) {
                sb.append("Meeting     : ").append(m.getTitle()).append(" (").append(m.getMeetingType()).append(")\n");
                sb.append("Scheduled   : ").append(m.getMeetingDate()).append(" at ").append(m.getMeetingTime()).append(" | Status: ").append(m.getStatus()).append("\n");
                sb.append("-----------------------------------------------------------------------------------------\n");
            }
        }

        sb.append("\n=========================================================================================\n");
        sb.append("                      END OF STATUTORY REPORT DOCUMENT SUMMARY                      \n");
        sb.append("=========================================================================================\n");
        return sb.toString();
    }

    private String cleanCsv(String val) {
        if (val == null) return "\"\"";
        return "\"" + val.replace("\"", "\"\"") + "\"";
    }

    public static class ExportLog {
        private final String name;
        private final String format;
        private final String timestamp;

        public ExportLog(String name, String format, String timestamp) {
            this.name = name;
            this.format = format;
            this.timestamp = timestamp;
        }

        public String getName() {
            return name;
        }

        public String getFormat() {
            return format;
        }

        public String getTimestamp() {
            return timestamp;
        }
    }
}
