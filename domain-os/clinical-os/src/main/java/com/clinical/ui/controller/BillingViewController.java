package com.clinical.ui.controller;

import com.clinical.billing.entity.InvoiceEntity;
import com.clinical.billing.service.BillingService;
import com.clinical.lab.entity.LabOrderEntity;
import com.clinical.lab.service.LabService;
import com.clinical.patient.entity.PatientEntity;
import com.clinical.patient.service.PatientService;
import com.clinical.pharmacy.entity.PharmacyInvoiceEntity;
import com.clinical.pharmacy.service.PharmacyService;
import com.clinical.report.service.ReportPrintingService;
import com.clinical.visit.entity.VisitEntity;
import com.clinical.visit.service.VisitService;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.web.WebView;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class BillingViewController {

    private final BillingService billingService;
    private final VisitService visitService;
    private final PatientService patientService;
    private final LabService labService;
    private final PharmacyService pharmacyService;
    private final ReportPrintingService reportPrintingService;

    // FXML Controls - Tab 1 Billing Counter
    @FXML private ComboBox<String> visitSelectorCombo;
    @FXML private Label patientNameLabel;
    @FXML private Label visitIdLabel;
    @FXML private Label doctorNameLabel;

    @FXML private TableView<FeeItem> feeBreakdownTable;
    @FXML private TableColumn<FeeItem, String> colDept;
    @FXML private TableColumn<FeeItem, String> colDesc;
    @FXML private TableColumn<FeeItem, String> colAmount;

    @FXML private TextField consultationFeeField;
    @FXML private TextField labFeeField;
    @FXML private TextField pharmacyFeeField;
    @FXML private Label subtotalLabel;
    @FXML private TextField discountField;
    @FXML private Label taxLabel;
    @FXML private Label netTotalLabel;
    @FXML private ComboBox<String> paymentModeCombo;
    @FXML private TextField remarksField;

    // FXML Controls - Tab 2 History & Day-End Register
    @FXML private Label statNetRevenueLabel;
    @FXML private Label statCashLabel;
    @FXML private Label statUpiLabel;
    @FXML private Label statCardLabel;
    @FXML textStatDiscountLabel;
    @FXML private Label statDiscountLabel;

    @FXML private TableView<InvoiceEntity> historyTable;
    @FXML private TableColumn<InvoiceEntity, String> colHistInvId;
    @FXML private TableColumn<InvoiceEntity, String> colHistVisitId;
    @FXML private TableColumn<InvoiceEntity, String> colHistPatient;
    @FXML private TableColumn<InvoiceEntity, String> colHistDoctor;
    @FXML private TableColumn<InvoiceEntity, String> colHistConsult;
    @FXML private TableColumn<InvoiceEntity, String> colHistLab;
    @FXML private TableColumn<InvoiceEntity, String> colHistPharm;
    @FXML private TableColumn<InvoiceEntity, String> colHistSubtotal;
    @FXML private TableColumn<InvoiceEntity, String> colHistDisc;
    @FXML private TableColumn<InvoiceEntity, String> colHistNetPaid;
    @FXML private TableColumn<InvoiceEntity, String> colHistMode;
    @FXML private TableColumn<InvoiceEntity, String> colHistDate;

    private final ObservableList<FeeItem> feeItems = FXCollections.observableArrayList();
    private final ObservableList<InvoiceEntity> invoiceHistory = FXCollections.observableArrayList();
    private InvoiceEntity lastGeneratedInvoice = null;

    public BillingViewController(BillingService billingService,
                                VisitService visitService,
                                PatientService patientService,
                                LabService labService,
                                PharmacyService pharmacyService,
                                ReportPrintingService reportPrintingService) {
        this.billingService = billingService;
        this.visitService = visitService;
        this.patientService = patientService;
        this.labService = labService;
        this.pharmacyService = pharmacyService;
        this.reportPrintingService = reportPrintingService;
    }

    @FXML
    public void initialize() {
        if (paymentModeCombo != null) {
            paymentModeCombo.setItems(FXCollections.observableArrayList("CASH", "UPI", "CARD", "NET_BANKING"));
            paymentModeCombo.setValue("CASH");
        }

        setupFeeTable();
        setupHistoryTable();
        loadActiveVisitsCombo();
        refreshCashRegisterHistory();
    }

    private void setupFeeTable() {
        if (feeBreakdownTable == null) return;
        colDept.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDepartment()));
        colDesc.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescription()));
        colAmount.setCellValueFactory(data -> new SimpleStringProperty("₹ " + data.getValue().getAmount().setScale(2, RoundingMode.HALF_UP)));
        feeBreakdownTable.setItems(feeItems);
    }

    private void setupHistoryTable() {
        if (historyTable == null) return;
        colHistInvId.setCellValueFactory(new PropertyValueFactory<>("invoiceId"));
        colHistVisitId.setCellValueFactory(new PropertyValueFactory<>("visitId"));
        colHistPatient.setCellValueFactory(new PropertyValueFactory<>("patientId"));
        colHistDoctor.setCellValueFactory(new PropertyValueFactory<>("doctorName"));
        colHistConsult.setCellValueFactory(data -> new SimpleStringProperty("₹ " + (data.getValue().getConsultationFee() != null ? data.getValue().getConsultationFee() : "0.00")));
        colHistLab.setCellValueFactory(data -> new SimpleStringProperty("₹ " + (data.getValue().getLabFee() != null ? data.getValue().getLabFee() : "0.00")));
        colHistPharm.setCellValueFactory(data -> new SimpleStringProperty("₹ " + (data.getValue().getPharmacyFee() != null ? data.getValue().getPharmacyFee() : "0.00")));
        colHistSubtotal.setCellValueFactory(data -> new SimpleStringProperty("₹ " + (data.getValue().getSubtotal() != null ? data.getValue().getSubtotal() : "0.00")));
        colHistDisc.setCellValueFactory(data -> new SimpleStringProperty("₹ " + (data.getValue().getDiscountAmount() != null ? data.getValue().getDiscountAmount() : "0.00")));
        colHistNetPaid.setCellValueFactory(data -> new SimpleStringProperty("₹ " + (data.getValue().getTotalAmount() != null ? data.getValue().getTotalAmount() : "0.00")));
        colHistMode.setCellValueFactory(new PropertyValueFactory<>("paymentMode"));
        colHistDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCreatedAt() != null ? data.getValue().getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "-"));

        historyTable.setItems(invoiceHistory);
    }

    private void loadActiveVisitsCombo() {
        if (visitSelectorCombo == null) return;
        List<VisitEntity> visits = visitService.getAllVisits();
        ObservableList<String> options = FXCollections.observableArrayList();
        for (VisitEntity v : visits) {
            String pName = "Patient " + v.getPatientId();
            try {
                PatientEntity p = patientService.getPatientById(v.getPatientId()).orElse(null);
                if (p != null) pName = p.getFirstName() + " " + p.getLastName();
            } catch (Exception ignored) {}
            options.add(v.getVisitId() + " - " + pName + " (" + (v.getDoctorId() != null ? v.getDoctorId() : "General OPD") + ")");
        }
        visitSelectorCombo.setItems(options);
        if (!options.isEmpty()) {
            visitSelectorCombo.getSelectionModel().select(0);
            handleFetchVisitFees();
        }
    }

    @FXML
    public void handleFetchVisitFees() {
        if (visitSelectorCombo == null || visitSelectorCombo.getValue() == null) return;
        String selected = visitSelectorCombo.getValue();
        String visitId = selected.split(" - ")[0].trim();

        VisitEntity visit = visitService.getAllVisits().stream()
                .filter(v -> v.getVisitId().equals(visitId))
                .findFirst().orElse(null);

        if (visit == null) return;

        visitIdLabel.setText(visit.getVisitId());
        doctorNameLabel.setText(visit.getDoctorId() != null ? visit.getDoctorId() : "General OPD Doctor");

        PatientEntity patient = patientService.getPatientById(visit.getPatientId()).orElse(null);
        if (patient != null) {
            patientNameLabel.setText(patient.getFirstName() + " " + patient.getLastName() + " (" + patient.getPatientId() + ")");
        } else {
            patientNameLabel.setText("Patient " + visit.getPatientId());
        }

        // Auto-Fetch Department Fees
        BigDecimal consultFee = new BigDecimal("500.00");
        BigDecimal labTotal = BigDecimal.ZERO;
        BigDecimal pharmTotal = BigDecimal.ZERO;

        feeItems.clear();

        // 1. Consultation Item
        feeItems.add(new FeeItem("Doctor OPD Consultation", "Specialist OPD Visit Consultation", consultFee));

        // 2. Lab Requisitions Total
        try {
            List<LabOrderEntity> labOrders = labService.getLabOrdersByVisit(visitId);
            StringBuilder labDesc = new StringBuilder();
            for (LabOrderEntity lo : labOrders) {
                if (lo.getCost() != null) labTotal = labTotal.add(lo.getCost());
                if (labDesc.length() > 0) labDesc.append(", ");
                labDesc.append(lo.getTestName());
            }
            if (labTotal.compareTo(BigDecimal.ZERO) > 0) {
                feeItems.add(new FeeItem("Laboratory & Diagnostics", labDesc.toString(), labTotal));
            }
        } catch (Exception ignored) {}

        // 3. Pharmacy Dispensing Total
        try {
            List<PharmacyInvoiceEntity> pharmInvoices = pharmacyService.getInvoicesByVisit(visitId);
            for (PharmacyInvoiceEntity pi : pharmInvoices) {
                if (pi.getGrandTotal() != null) pharmTotal = pharmTotal.add(pi.getGrandTotal());
            }
            if (pharmTotal.compareTo(BigDecimal.ZERO) > 0) {
                feeItems.add(new FeeItem("Pharmacy & Medications", "Dispensed Medication Items", pharmTotal));
            }
        } catch (Exception ignored) {}

        consultationFeeField.setText(consultFee.setScale(2, RoundingMode.HALF_UP).toString());
        labFeeField.setText(labTotal.setScale(2, RoundingMode.HALF_UP).toString());
        pharmacyFeeField.setText(pharmTotal.setScale(2, RoundingMode.HALF_UP).toString());

        recalculateTotals();
    }

    @FXML
    public void recalculateTotals() {
        try {
            BigDecimal cFee = parseBigDecimal(consultationFeeField.getText());
            BigDecimal lFee = parseBigDecimal(labFeeField.getText());
            BigDecimal pFee = parseBigDecimal(pharmacyFeeField.getText());

            BigDecimal subtotal = cFee.add(lFee).add(pFee);
            BigDecimal discount = parseBigDecimal(discountField.getText());
            BigDecimal afterDisc = subtotal.subtract(discount);
            if (afterDisc.compareTo(BigDecimal.ZERO) < 0) afterDisc = BigDecimal.ZERO;
            BigDecimal tax = afterDisc.multiply(new BigDecimal("0.05"));
            BigDecimal netTotal = afterDisc.add(tax);

            subtotalLabel.setText("₹ " + subtotal.setScale(2, RoundingMode.HALF_UP));
            taxLabel.setText("₹ " + tax.setScale(2, RoundingMode.HALF_UP));
            netTotalLabel.setText("₹ " + netTotal.setScale(2, RoundingMode.HALF_UP));
        } catch (Exception ignored) {}
    }

    @FXML
    public void handleGenerateMasterInvoice() {
        if (visitSelectorCombo == null || visitSelectorCombo.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "No Visit Selected", "Please select a patient visit to issue a Master OPD invoice.");
            return;
        }

        String visitId = visitIdLabel.getText();
        VisitEntity visit = visitService.getAllVisits().stream()
                .filter(v -> v.getVisitId().equals(visitId))
                .findFirst().orElse(null);

        if (visit == null) return;

        BigDecimal cFee = parseBigDecimal(consultationFeeField.getText());
        BigDecimal lFee = parseBigDecimal(labFeeField.getText());
        BigDecimal pFee = parseBigDecimal(pharmacyFeeField.getText());
        BigDecimal discount = parseBigDecimal(discountField.getText());
        String mode = paymentModeCombo.getValue() != null ? paymentModeCombo.getValue() : "CASH";
        String remarks = remarksField != null ? remarksField.getText() : "Settled at OPD Counter";

        lastGeneratedInvoice = billingService.generateMasterOpdInvoice(
                visitId,
                visit.getPatientId(),
                doctorNameLabel.getText(),
                cFee, lFee, pFee,
                discount, mode, remarks
        );

        showAlert(Alert.AlertType.INFORMATION, "Master OPD Invoice Settled", "Master OPD Invoice " + lastGeneratedInvoice.getInvoiceId() + " generated & settled successfully!");

        refreshCashRegisterHistory();
        handlePrintCurrentInvoice();
    }

    @FXML
    public void handlePrintCurrentInvoice() {
        if (lastGeneratedInvoice == null) {
            List<InvoiceEntity> all = billingService.getAllInvoices();
            if (!all.isEmpty()) lastGeneratedInvoice = all.get(all.size() - 1);
        }

        if (lastGeneratedInvoice == null) {
            showAlert(Alert.AlertType.WARNING, "No Invoice Available", "No master invoice generated to print.");
            return;
        }

        printMasterInvoice(lastGeneratedInvoice);
    }

    @FXML
    public void handleReprintSelectedInvoice() {
        InvoiceEntity selected = historyTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Invoice Selected", "Please select an invoice from the history table to reprint.");
            return;
        }

        printMasterInvoice(selected);
    }

    private void printMasterInvoice(InvoiceEntity invoice) {
        PatientEntity patient = patientService.getPatientById(invoice.getPatientId()).orElse(null);
        List<LabOrderEntity> labOrders = labService.getLabOrdersByVisit(invoice.getVisitId());
        List<PharmacyInvoiceEntity> pharmInvoices = pharmacyService.getInvoicesByVisit(invoice.getVisitId());

        String html = reportPrintingService.generateMasterOpdInvoiceHtml(invoice, patient, labOrders, pharmInvoices);

        WebView webView = new WebView();
        webView.getEngine().loadContent(html);

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Unified OPD Master Hospital Bill - " + invoice.getInvoiceId());
        dialog.getDialogPane().setContent(webView);
        dialog.getDialogPane().setPrefSize(850, 650);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    @FXML
    public void handleRefreshAll() {
        loadActiveVisitsCombo();
        refreshCashRegisterHistory();
    }

    private void refreshCashRegisterHistory() {
        List<InvoiceEntity> invoices = billingService.getAllInvoices();
        invoiceHistory.setAll(invoices);

        Map<String, Object> summary = billingService.getDailyCashRegisterSummary(LocalDate.now());
        if (statNetRevenueLabel != null) statNetRevenueLabel.setText("₹ " + formatDecimal(summary.get("netRevenue")));
        if (statCashLabel != null) statCashLabel.setText("₹ " + formatDecimal(summary.get("cashTotal")));
        if (statUpiLabel != null) statUpiLabel.setText("₹ " + formatDecimal(summary.get("upiTotal")));
        if (statCardLabel != null) statCardLabel.setText("₹ " + formatDecimal(summary.get("cardTotal")));
        if (statDiscountLabel != null) statDiscountLabel.setText("₹ " + formatDecimal(summary.get("discountTotal")));
    }

    private BigDecimal parseBigDecimal(String str) {
        if (str == null || str.trim().isEmpty()) return BigDecimal.ZERO;
        try {
            return new BigDecimal(str.trim());
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private String formatDecimal(Object obj) {
        if (obj instanceof BigDecimal) {
            return ((BigDecimal) obj).setScale(2, RoundingMode.HALF_UP).toString();
        }
        return "0.00";
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static class FeeItem {
        private final String department;
        private final String description;
        private final BigDecimal amount;

        public FeeItem(String department, String description, BigDecimal amount) {
            this.department = department;
            this.description = description;
            this.amount = amount;
        }

        public String getDepartment() { return department; }
        public String getDescription() { return description; }
        public BigDecimal getAmount() { return amount; }
    }
}
