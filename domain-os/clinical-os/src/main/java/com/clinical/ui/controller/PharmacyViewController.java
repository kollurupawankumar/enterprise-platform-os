package com.clinical.ui.controller;

import com.clinical.doctor.service.DoctorService;
import com.clinical.patient.entity.PatientEntity;
import com.clinical.patient.service.PatientService;
import com.clinical.pharmacy.entity.MedicineInventoryEntity;
import com.clinical.pharmacy.entity.PharmacyInvoiceEntity;
import com.clinical.pharmacy.entity.PharmacyInvoiceItemEntity;
import com.clinical.pharmacy.service.PharmacyService;
import com.clinical.prescription.entity.PrescriptionEntity;
import com.clinical.prescription.entity.PrescriptionItemEntity;
import com.clinical.prescription.repository.PrescriptionRepository;
import com.clinical.prescription.service.PrescriptionService;
import com.clinical.report.service.ReportPrintingService;
import com.clinical.visit.entity.VisitEntity;
import com.clinical.visit.service.VisitService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.web.WebView;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PharmacyViewController {

    private final PharmacyService pharmacyService;
    private final PrescriptionService prescriptionService;
    private final PrescriptionRepository prescriptionRepository;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final VisitService visitService;
    private final ReportPrintingService reportPrintingService;

    // Cart Controls
    @FXML private ComboBox<String> prescriptionQueueCombo;
    @FXML private ComboBox<String> patientCombo;
    @FXML private ComboBox<String> inventoryPickerCombo;
    @FXML private TextField qtyField;
    @FXML private ListView<String> cartListView;

    @FXML private Label subtotalLabel;
    @FXML private TextField discountField;
    @FXML private Label taxLabel;
    @FXML private Label totalPayableLabel;
    @FXML private ComboBox<String> paymentModeCombo;

    // Inventory Stock Table
    @FXML private TableView<MedicineInventoryEntity> inventoryTable;
    @FXML private TableColumn<MedicineInventoryEntity, String> codeCol;
    @FXML private TableColumn<MedicineInventoryEntity, String> nameCol;
    @FXML private TableColumn<MedicineInventoryEntity, String> categoryCol;
    @FXML private TableColumn<MedicineInventoryEntity, String> batchCol;
    @FXML private TableColumn<MedicineInventoryEntity, String> expiryCol;
    @FXML private TableColumn<MedicineInventoryEntity, Integer> qtyCol;
    @FXML private TableColumn<MedicineInventoryEntity, String> priceCol;
    @FXML private TableColumn<MedicineInventoryEntity, String> statusCol;

    // Add Stock Pane
    @FXML private TextField addCodeField;
    @FXML private TextField addNameField;
    @FXML private TextField addBatchField;
    @FXML private TextField addExpiryField;
    @FXML private TextField addQtyField;
    @FXML private TextField addPriceField;

    @FXML private WebView reportWebView;

    private final List<PharmacyInvoiceItemEntity> cartItems = new ArrayList<>();
    private Map<String, MedicineInventoryEntity> stockCache;
    private Map<String, PatientEntity> patientCache;
    private Map<String, PrescriptionEntity> prescriptionMap;

    private PrescriptionEntity activePrescription;
    private PharmacyInvoiceEntity lastIssuedInvoice;

    public PharmacyViewController(PharmacyService pharmacyService,
                                  PrescriptionService prescriptionService,
                                  PrescriptionRepository prescriptionRepository,
                                  PatientService patientService,
                                  DoctorService doctorService,
                                  VisitService visitService,
                                  ReportPrintingService reportPrintingService) {
        this.pharmacyService = pharmacyService;
        this.prescriptionService = prescriptionService;
        this.prescriptionRepository = prescriptionRepository;
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.visitService = visitService;
        this.reportPrintingService = reportPrintingService;
    }

    @FXML
    public void initialize() {
        setupPrescriptionQueueCombo();
        setupPatientCombo();
        setupInventoryPickerCombo();
        setupPaymentModeCombo();
        setupInventoryTable();
        loadInventoryStock();

        if (discountField != null) {
            discountField.textProperty().addListener((o, oldV, newV) -> calculateCartTotals());
        }
    }

    private void setupPrescriptionQueueCombo() {
        if (prescriptionQueueCombo == null) return;
        List<PrescriptionEntity> prescriptions = prescriptionRepository.findAll().stream()
                .filter(p -> !"DISPENSED".equalsIgnoreCase(p.getStatus()))
                .collect(Collectors.toList());

        prescriptionMap = prescriptions.stream().collect(Collectors.toMap(PrescriptionEntity::getPrescriptionId, p -> p, (p1, p2) -> p1));

        Map<String, VisitEntity> visitCache = visitService.getAllVisits().stream()
                .collect(Collectors.toMap(VisitEntity::getVisitId, v -> v, (v1, v2) -> v1));
        Map<String, PatientEntity> pMap = patientService.getAllPatients().stream()
                .collect(Collectors.toMap(PatientEntity::getPatientId, p -> p, (p1, p2) -> p1));

        List<String> items = prescriptions.stream().map(p -> {
            VisitEntity v = visitCache.get(p.getVisitId());
            String pName = "Patient";
            if (v != null && pMap.containsKey(v.getPatientId())) {
                PatientEntity pe = pMap.get(v.getPatientId());
                pName = pe.getFirstName() + " " + pe.getLastName();
            }
            return p.getPrescriptionId() + " - " + pName + " (" + (p.getItems() != null ? p.getItems().size() : 0) + " Rx items)";
        }).collect(Collectors.toList());

        prescriptionQueueCombo.setItems(FXCollections.observableArrayList(items));
    }

    private void setupPatientCombo() {
        if (patientCombo == null) return;
        patientCache = patientService.getAllPatients().stream()
                .collect(Collectors.toMap(PatientEntity::getPatientId, p -> p, (p1, p2) -> p1));

        List<String> displayList = new ArrayList<>();
        displayList.add("Walk-in OTC Customer");
        patientCache.values().forEach(p -> displayList.add(p.getPatientId() + " - " + p.getFirstName() + " " + p.getLastName() + " (" + p.getPhone() + ")"));

        patientCombo.setItems(FXCollections.observableArrayList(displayList));
        patientCombo.getSelectionModel().selectFirst();
    }

    private void setupInventoryPickerCombo() {
        if (inventoryPickerCombo == null) return;
        List<MedicineInventoryEntity> allStock = pharmacyService.getAllStock();
        stockCache = allStock.stream().collect(Collectors.toMap(
                s -> (s.getMedicineCode() != null ? s.getMedicineCode() : s.getMedicineName()) + "|" + s.getBatchNumber(),
                s -> s,
                (s1, s2) -> s1
        ));

        List<String> pickerItems = allStock.stream()
                .map(s -> s.getMedicineName() + " (Batch: " + s.getBatchNumber() + " | Stock: " + s.getQuantity() + " | ₹ " + s.getSellingPrice() + ")")
                .collect(Collectors.toList());

        inventoryPickerCombo.setItems(FXCollections.observableArrayList(pickerItems));
        if (!pickerItems.isEmpty()) inventoryPickerCombo.getSelectionModel().selectFirst();
    }

    private void setupPaymentModeCombo() {
        if (paymentModeCombo == null) return;
        paymentModeCombo.setItems(FXCollections.observableArrayList("CASH", "UPI", "CARD", "BILLING_DESK"));
        paymentModeCombo.getSelectionModel().selectFirst();
    }

    private void setupInventoryTable() {
        if (inventoryTable == null) return;
        codeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMedicineCode() != null ? data.getValue().getMedicineCode() : "MED-100"));
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMedicineName()));
        categoryCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCategory() != null ? data.getValue().getCategory() : "General"));
        batchCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBatchNumber()));
        expiryCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getExpiryDate() != null ? data.getValue().getExpiryDate().toString() : "N/A"));
        qtyCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getQuantity()));
        priceCol.setCellValueFactory(data -> new SimpleStringProperty("₹ " + data.getValue().getSellingPrice()));
        statusCol.setCellValueFactory(data -> {
            int qty = data.getValue().getQuantity();
            if (qty <= 0) return new SimpleStringProperty("🔴 Out of Stock");
            if (qty <= (data.getValue().getReorderLevel() != null ? data.getValue().getReorderLevel() : 20)) return new SimpleStringProperty("⚠️ Low Stock");
            return new SimpleStringProperty("✓ In Stock");
        });
    }

    @FXML
    public void loadInventoryStock() {
        if (inventoryTable != null) {
            inventoryTable.setItems(FXCollections.observableArrayList(pharmacyService.getAllStock()));
        }
        setupInventoryPickerCombo();
    }

    @FXML
    public void handleLoadPrescription() {
        String selected = prescriptionQueueCombo != null ? prescriptionQueueCombo.getValue() : null;
        if (selected == null) return;

        String rxId = selected.split(" - ")[0];
        this.activePrescription = prescriptionMap.get(rxId);
        if (activePrescription == null || activePrescription.getItems() == null) return;

        cartItems.clear();
        List<MedicineInventoryEntity> allStock = pharmacyService.getAllStock();

        for (PrescriptionItemEntity rxItem : activePrescription.getItems()) {
            Optional<MedicineInventoryEntity> optStock = allStock.stream()
                    .filter(s -> s.getMedicineName().equalsIgnoreCase(rxItem.getMedicineName()) && s.getQuantity() > 0)
                    .findFirst();

            PharmacyInvoiceItemEntity item = new PharmacyInvoiceItemEntity();
            item.setMedicineName(rxItem.getMedicineName());
            item.setDosageInstruction(rxItem.getDose() + " (" + rxItem.getDuration() + ")");
            item.setQuantity(10); // Standard strip quantity

            if (optStock.isPresent()) {
                MedicineInventoryEntity stock = optStock.get();
                item.setMedicineCode(stock.getMedicineCode());
                item.setBatchNumber(stock.getBatchNumber());
                item.setExpiryDate(stock.getExpiryDate() != null ? stock.getExpiryDate().toString() : "N/A");
                item.setUnitPrice(stock.getSellingPrice());
            } else {
                item.setMedicineCode("MED-GEN");
                item.setBatchNumber("GENERIC-01");
                item.setExpiryDate("N/A");
                item.setUnitPrice(new BigDecimal("5.00"));
            }
            item.setLineTotal(item.getUnitPrice().multiply(new BigDecimal(item.getQuantity())));
            cartItems.add(item);
        }

        // Set patient dropdown if visit matches
        if (activePrescription.getVisitId() != null) {
            visitService.getVisitById(activePrescription.getVisitId()).ifPresent(v -> {
                if (patientCombo != null && v.getPatientId() != null) {
                    patientCombo.getItems().stream()
                            .filter(i -> i.startsWith(v.getPatientId()))
                            .findFirst()
                            .ifPresent(i -> patientCombo.getSelectionModel().select(i));
                }
            });
        }

        updateCartUI();
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Loaded " + cartItems.size() + " prescription item(s) into cart.", ButtonType.OK);
        alert.show();
    }

    @FXML
    public void handleAddToCart() {
        String selected = inventoryPickerCombo != null ? inventoryPickerCombo.getValue() : null;
        if (selected == null) return;

        int qty = 1;
        try {
            if (qtyField != null && !qtyField.getText().isEmpty()) {
                qty = Integer.parseInt(qtyField.getText());
            }
        } catch (Exception ignored) {}

        String medName = selected.split(" \\(Batch:")[0];
        List<MedicineInventoryEntity> stockList = pharmacyService.getAllStock();
        Optional<MedicineInventoryEntity> optStock = stockList.stream()
                .filter(s -> s.getMedicineName().equalsIgnoreCase(medName))
                .findFirst();

        if (optStock.isPresent()) {
            MedicineInventoryEntity stock = optStock.get();
            PharmacyInvoiceItemEntity item = new PharmacyInvoiceItemEntity();
            item.setMedicineCode(stock.getMedicineCode());
            item.setMedicineName(stock.getMedicineName());
            item.setBatchNumber(stock.getBatchNumber());
            item.setExpiryDate(stock.getExpiryDate() != null ? stock.getExpiryDate().toString() : "N/A");
            item.setQuantity(qty);
            item.setUnitPrice(stock.getSellingPrice());
            item.setLineTotal(stock.getSellingPrice().multiply(new BigDecimal(qty)));
            item.setDosageInstruction("As directed");
            cartItems.add(item);
            updateCartUI();
        }
    }

    @FXML
    public void handleClearCart() {
        cartItems.clear();
        this.activePrescription = null;
        updateCartUI();
    }

    private void updateCartUI() {
        if (cartListView == null) return;
        List<String> displayStrings = cartItems.stream()
                .map(i -> i.getMedicineName() + " (Batch: " + i.getBatchNumber() + ") x " + i.getQuantity() + " @ ₹" + i.getUnitPrice() + " = ₹" + i.getLineTotal())
                .collect(Collectors.toList());

        cartListView.setItems(FXCollections.observableArrayList(displayStrings));
        calculateCartTotals();
    }

    private void calculateCartTotals() {
        BigDecimal subtotal = cartItems.stream().map(PharmacyInvoiceItemEntity::getLineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal disc = BigDecimal.ZERO;
        try {
            if (discountField != null && !discountField.getText().isEmpty()) {
                disc = new BigDecimal(discountField.getText().trim());
            }
        } catch (Exception ignored) {}

        BigDecimal afterDisc = subtotal.subtract(disc);
        if (afterDisc.compareTo(BigDecimal.ZERO) < 0) afterDisc = BigDecimal.ZERO;
        BigDecimal tax = afterDisc.multiply(new BigDecimal("0.05"));
        BigDecimal total = afterDisc.add(tax);

        if (subtotalLabel != null) subtotalLabel.setText("₹ " + subtotal.toString());
        if (taxLabel != null) taxLabel.setText("₹ " + String.format("%.2f", tax));
        if (totalPayableLabel != null) totalPayableLabel.setText("₹ " + String.format("%.2f", total));
    }

    @FXML
    public void handlePayAndDispense() {
        if (cartItems.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Cart is empty. Please add medicines or load a doctor prescription first.", ButtonType.OK);
            alert.show();
            return;
        }

        String pSel = patientCombo != null ? patientCombo.getValue() : null;
        String patientId = (pSel != null && !pSel.startsWith("Walk-in")) ? pSel.split(" - ")[0] : null;

        String visitId = activePrescription != null ? activePrescription.getVisitId() : null;
        String doctorName = activePrescription != null ? "Dr. Prescriber" : "Self / OTC";

        BigDecimal disc = BigDecimal.ZERO;
        try {
            if (discountField != null && !discountField.getText().isEmpty()) disc = new BigDecimal(discountField.getText());
        } catch (Exception ignored) {}

        String payMode = paymentModeCombo != null ? paymentModeCombo.getValue() : "CASH";
        String payStatus = "BILLING_DESK".equalsIgnoreCase(payMode) ? "UNPAID" : "PAID";

        PharmacyInvoiceEntity invoice = pharmacyService.createPharmacyInvoice(patientId, visitId, doctorName, cartItems, disc, payStatus, payMode);
        this.lastIssuedInvoice = invoice;

        if (activePrescription != null) {
            pharmacyService.dispensePrescription(activePrescription);
        }

        handleClearCart();
        loadInventoryStock();
        setupPrescriptionQueueCombo();

        renderPharmacyInvoicePreview(invoice);

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Pharmacy Invoice " + invoice.getInvoiceId() + " dispensed successfully! Total Paid: ₹ " + invoice.getTotalAmount(), ButtonType.OK);
        alert.show();
    }

    @FXML
    public void handleShowAddStockDialog() {
        // Toggle or clear add stock form
        if (addCodeField != null) addCodeField.setText("MED-109");
        if (addNameField != null) addNameField.setText("");
        if (addBatchField != null) addBatchField.setText("BATCH-2026-N1");
        if (addExpiryField != null) addExpiryField.setText("2028-12-31");
        if (addQtyField != null) addQtyField.setText("100");
        if (addPriceField != null) addPriceField.setText("10.00");
    }

    @FXML
    public void handleSaveStockItem() {
        if (addNameField == null || addNameField.getText().isEmpty()) return;

        MedicineInventoryEntity med = new MedicineInventoryEntity();
        med.setMedicineCode(addCodeField != null && !addCodeField.getText().isEmpty() ? addCodeField.getText() : "MED-NEW");
        med.setMedicineName(addNameField.getText());
        med.setBatchNumber(addBatchField != null && !addBatchField.getText().isEmpty() ? addBatchField.getText() : "BATCH-01");
        if (addExpiryField != null && !addExpiryField.getText().isEmpty()) {
            try { med.setExpiryDate(LocalDate.parse(addExpiryField.getText())); } catch (Exception ignored) {}
        }
        med.setQuantity(addQtyField != null && !addQtyField.getText().isEmpty() ? Integer.parseInt(addQtyField.getText()) : 100);
        med.setSellingPrice(addPriceField != null && !addPriceField.getText().isEmpty() ? new BigDecimal(addPriceField.getText()) : new BigDecimal("10.00"));

        pharmacyService.addOrUpdateStock(med);
        loadInventoryStock();

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Saved stock batch " + med.getMedicineName() + " (" + med.getBatchNumber() + ").", ButtonType.OK);
        alert.show();
    }

    @FXML
    public void handlePrintReceipt() {
        if (lastIssuedInvoice != null && reportWebView != null) {
            renderPharmacyInvoicePreview(lastIssuedInvoice);
            reportWebView.getEngine().print(null);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "No issued pharmacy bill loaded to print.", ButtonType.OK);
            alert.show();
        }
    }

    private void renderPharmacyInvoicePreview(PharmacyInvoiceEntity invoice) {
        if (reportWebView != null && invoice != null) {
            PatientEntity patient = invoice.getPatientId() != null && patientCache != null ? patientCache.get(invoice.getPatientId()) : null;
            String html = reportPrintingService.generatePharmacyInvoiceHtml(invoice, patient);
            reportWebView.getEngine().loadContent(html);
        }
    }
}

