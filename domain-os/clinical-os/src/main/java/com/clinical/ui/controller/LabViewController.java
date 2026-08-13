package com.clinical.ui.controller;

import com.clinical.doctor.service.DoctorService;
import com.clinical.lab.entity.LabOrderEntity;
import com.clinical.lab.entity.LabOrderItemEntity;
import com.clinical.lab.entity.LabTestCatalogEntity;
import com.clinical.lab.repository.LabOrderRepository;
import com.clinical.lab.service.LabDiagnosticsService;
import com.clinical.patient.entity.PatientEntity;
import com.clinical.patient.service.PatientService;
import com.clinical.report.service.ReportPrintingService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.web.WebView;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class LabViewController {

    private final LabDiagnosticsService labDiagnosticsService;
    private final LabOrderRepository labOrderRepository;
    private final ReportPrintingService reportPrintingService;
    private final PatientService patientService;
    private final DoctorService doctorService;

    // Cart & Requisition Controls
    @FXML private ComboBox<String> patientCombo;
    @FXML private ComboBox<String> doctorCombo;
    @FXML private ComboBox<String> catalogCombo;
    @FXML private ListView<String> cartListView;
    @FXML private Label cartTotalLabel;
    @FXML private ComboBox<String> paymentModeCombo;

    // Main Orders Table
    @FXML private TableView<LabOrderEntity> labOrderTable;
    @FXML private TableColumn<LabOrderEntity, String> orderIdCol;
    @FXML private TableColumn<LabOrderEntity, String> patientIdCol;
    @FXML private TableColumn<LabOrderEntity, String> testNameCol;
    @FXML private TableColumn<LabOrderEntity, String> totalAmountCol;
    @FXML private TableColumn<LabOrderEntity, String> paymentStatusCol;
    @FXML private TableColumn<LabOrderEntity, String> statusCol;

    // Itemized Parameter Results Table
    @FXML private TableView<LabOrderItemEntity> labItemTable;
    @FXML private TableColumn<LabOrderItemEntity, String> itemCodeCol;
    @FXML private TableColumn<LabOrderItemEntity, String> itemNameCol;
    @FXML private TableColumn<LabOrderItemEntity, String> itemResultCol;
    @FXML private TableColumn<LabOrderItemEntity, String> itemUnitsCol;
    @FXML private TableColumn<LabOrderItemEntity, String> itemRangeCol;
    @FXML private TableColumn<LabOrderItemEntity, String> itemFlagCol;
    @FXML private TableColumn<LabOrderItemEntity, String> itemStatusCol;

    @FXML private Label selectedItemLabel;
    @FXML private TextField resultValueField;
    @FXML private TextField remarksField;

    @FXML private WebView reportWebView;

    private final List<LabTestCatalogEntity> cartTests = new ArrayList<>();
    private Map<String, LabTestCatalogEntity> catalogMap;
    private Map<String, PatientEntity> patientCache;

    private LabOrderEntity selectedOrder;
    private LabOrderItemEntity selectedItem;

    public LabViewController(LabDiagnosticsService labDiagnosticsService,
                             LabOrderRepository labOrderRepository,
                             ReportPrintingService reportPrintingService,
                             PatientService patientService,
                             DoctorService doctorService) {
        this.labDiagnosticsService = labDiagnosticsService;
        this.labOrderRepository = labOrderRepository;
        this.reportPrintingService = reportPrintingService;
        this.patientService = patientService;
        this.doctorService = doctorService;
    }

    @FXML
    public void initialize() {
        setupPatientCombo();
        setupDoctorCombo();
        setupCatalogCombo();
        setupPaymentModeCombo();
        setupOrdersTable();
        setupItemsTable();
        loadAllLabOrders();
    }

    private void setupPatientCombo() {
        if (patientCombo == null) return;
        patientCache = patientService.getAllPatients().stream()
                .collect(Collectors.toMap(PatientEntity::getPatientId, p -> p, (p1, p2) -> p1));

        List<String> displayList = new ArrayList<>();
        displayList.add("Walk-in Patient (No ID)");
        patientCache.values().forEach(p -> displayList.add(p.getPatientId() + " - " + p.getFirstName() + " " + p.getLastName() + " (" + p.getPhone() + ")"));

        patientCombo.setItems(FXCollections.observableArrayList(displayList));
        patientCombo.getSelectionModel().selectFirst();
    }

    private void setupDoctorCombo() {
        if (doctorCombo == null) return;
        List<String> doctors = new ArrayList<>();
        doctors.add("Self / Walk-in");
        doctorService.getAllDoctors().forEach(d -> {
            String name = d.getDisplayName() != null ? d.getDisplayName() : d.getName();
            doctors.add(name + " (" + d.getSpecialization() + ")");
        });
        doctorCombo.setItems(FXCollections.observableArrayList(doctors));
        doctorCombo.getSelectionModel().selectFirst();
    }

    private void setupCatalogCombo() {
        if (catalogCombo == null) return;
        List<LabTestCatalogEntity> catalog = labDiagnosticsService.getTestCatalog();
        catalogMap = catalog.stream().collect(Collectors.toMap(LabTestCatalogEntity::getTestCode, t -> t, (t1, t2) -> t1));

        List<String> items = catalog.stream()
                .map(t -> t.getTestCode() + ": " + t.getTestName() + " (₹ " + t.getUnitPrice() + ")")
                .collect(Collectors.toList());

        catalogCombo.setItems(FXCollections.observableArrayList(items));
        if (!items.isEmpty()) catalogCombo.getSelectionModel().selectFirst();
    }

    private void setupPaymentModeCombo() {
        if (paymentModeCombo == null) return;
        paymentModeCombo.setItems(FXCollections.observableArrayList("CASH", "UPI", "CARD", "BILLING_DESK"));
        paymentModeCombo.getSelectionModel().selectFirst();
    }

    private void setupOrdersTable() {
        if (labOrderTable == null) return;
        orderIdCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getOrderId()));
        patientIdCol.setCellValueFactory(data -> {
            String pid = data.getValue().getPatientId();
            if (pid == null || pid.isBlank()) return new SimpleStringProperty("Walk-in Patient");
            PatientEntity p = patientCache != null ? patientCache.get(pid) : null;
            return new SimpleStringProperty(p != null ? p.getFirstName() + " " + p.getLastName() + " (" + pid + ")" : pid);
        });
        testNameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTestName()));
        totalAmountCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTotalAmount() != null ? "₹ " + data.getValue().getTotalAmount() : "₹ 0.00"));
        paymentStatusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPaymentStatus()));
        statusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));

        labOrderTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                this.selectedOrder = newSel;
                loadOrderItems(newSel.getOrderId());
                renderLabReportPreview(newSel);
            }
        });
    }

    private void setupItemsTable() {
        if (labItemTable == null) return;
        itemCodeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTestCode()));
        itemNameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTestName()));
        itemResultCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getResultValue() != null ? data.getValue().getResultValue() : "Pending"));
        itemUnitsCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUnits() != null ? data.getValue().getUnits() : "--"));
        itemRangeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNormalRange() != null ? data.getValue().getNormalRange() : "--"));
        itemFlagCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFlag() != null ? data.getValue().getFlag() : "NORMAL"));
        itemStatusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));

        labItemTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                this.selectedItem = newSel;
                if (selectedItemLabel != null) {
                    selectedItemLabel.setText("Editing: " + newSel.getTestName() + " (" + newSel.getTestCode() + ")");
                }
                if (resultValueField != null) resultValueField.setText(newSel.getResultValue() != null ? newSel.getResultValue() : "");
                if (remarksField != null) remarksField.setText(newSel.getRemarks() != null ? newSel.getRemarks() : "");
            }
        });
    }

    @FXML
    public void handleAddToCart() {
        String selected = catalogCombo != null ? catalogCombo.getValue() : null;
        if (selected == null) return;
        String code = selected.split(":")[0];
        LabTestCatalogEntity test = catalogMap.get(code);
        if (test != null && !cartTests.contains(test)) {
            cartTests.add(test);
            updateCartUI();
        }
    }

    @FXML
    public void handleClearCart() {
        cartTests.clear();
        updateCartUI();
    }

    private void updateCartUI() {
        if (cartListView == null) return;
        List<String> items = cartTests.stream()
                .map(t -> t.getTestCode() + ": " + t.getTestName() + " - ₹ " + t.getUnitPrice())
                .collect(Collectors.toList());

        cartListView.setItems(FXCollections.observableArrayList(items));

        BigDecimal total = cartTests.stream().map(LabTestCatalogEntity::getUnitPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (cartTotalLabel != null) cartTotalLabel.setText("₹ " + total.toString());
    }

    @FXML
    public void handleCreateOrderAndBill() {
        if (cartTests.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Cart is empty. Please select diagnostic tests from the catalog first.", ButtonType.OK);
            alert.show();
            return;
        }

        String pSel = patientCombo != null ? patientCombo.getValue() : null;
        String patientId = (pSel != null && !pSel.startsWith("Walk-in")) ? pSel.split(" - ")[0] : null;

        String docSel = doctorCombo != null ? doctorCombo.getValue() : "Self / Walk-in";
        String doctorName = docSel != null ? docSel.split(" \\(")[0] : "Self / Walk-in";

        String payMode = paymentModeCombo != null ? paymentModeCombo.getValue() : "CASH";
        String payStatus = "BILLING_DESK".equalsIgnoreCase(payMode) ? "UNPAID" : "PAID";

        LabOrderEntity order = labDiagnosticsService.createMultiTestOrder(patientId, null, doctorName, cartTests, payStatus, payMode);

        handleClearCart();
        loadAllLabOrders();

        renderLabReportPreview(order);

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Lab Order " + order.getOrderId() + " created successfully! Total Amount: ₹ " + order.getTotalAmount(), ButtonType.OK);
        alert.show();
    }

    private void loadOrderItems(String orderId) {
        if (labItemTable == null) return;
        List<LabOrderItemEntity> items = labDiagnosticsService.getOrderItems(orderId);
        labItemTable.setItems(FXCollections.observableArrayList(items));
    }

    @FXML
    public void handleSaveItemResult() {
        if (selectedItem == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a test parameter line item from the table first.", ButtonType.OK);
            alert.show();
            return;
        }
        String val = resultValueField != null ? resultValueField.getText() : "";
        String rem = remarksField != null ? remarksField.getText() : "";
        labDiagnosticsService.updateItemResult(selectedItem.getId(), val, rem, "COMPLETED");

        if (selectedOrder != null) {
            loadOrderItems(selectedOrder.getOrderId());
            renderLabReportPreview(selectedOrder);
        }
        loadAllLabOrders();
    }

    @FXML
    public void handlePrintCertificate() {
        if (selectedOrder != null && reportWebView != null) {
            renderLabReportPreview(selectedOrder);
            reportWebView.getEngine().print(null);
        }
    }

    @FXML
    public void handlePrintReceipt() {
        if (selectedOrder != null && reportWebView != null) {
            PatientEntity patient = selectedOrder.getPatientId() != null ? patientCache.get(selectedOrder.getPatientId()) : null;
            String receiptHtml = reportPrintingService.generateLabReceiptHtml(selectedOrder, patient);
            reportWebView.getEngine().loadContent(receiptHtml);
            reportWebView.getEngine().print(null);
        }
    }

    @FXML
    public void loadAllLabOrders() {
        if (labOrderTable != null) {
            List<LabOrderEntity> orders = labDiagnosticsService.getAllOrders();
            labOrderTable.setItems(FXCollections.observableArrayList(orders));
            if (!orders.isEmpty() && selectedOrder == null) {
                labOrderTable.getSelectionModel().selectFirst();
            }
        }
    }

    private void renderLabReportPreview(LabOrderEntity order) {
        if (reportWebView != null && order != null) {
            PatientEntity patient = order.getPatientId() != null && patientCache != null ? patientCache.get(order.getPatientId()) : null;
            String html = reportPrintingService.generateLabReportHtml(order, patient);
            reportWebView.getEngine().loadContent(html);
        }
    }
}

