package com.clinical.ui.controller;

import com.clinical.lab.entity.LabOrderEntity;
import com.clinical.lab.repository.LabOrderRepository;
import com.clinical.lab.service.LabDiagnosticsService;
import com.clinical.report.service.ReportPrintingService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.web.WebView;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LabViewController {

    private final LabDiagnosticsService labDiagnosticsService;
    private final LabOrderRepository labOrderRepository;
    private final ReportPrintingService reportPrintingService;

    @FXML private TableView<LabOrderEntity> labOrderTable;
    @FXML private TableColumn<LabOrderEntity, String> orderIdCol;
    @FXML private TableColumn<LabOrderEntity, String> visitIdCol;
    @FXML private TableColumn<LabOrderEntity, String> testNameCol;
    @FXML private TableColumn<LabOrderEntity, String> statusCol;
    @FXML private TableColumn<LabOrderEntity, String> resultCol;

    @FXML private TextField visitIdField;
    @FXML private TextField testNameField;
    @FXML private TextField selectedOrderField;
    @FXML private ComboBox<String> statusCombo;
    @FXML private TextArea resultArea;
    @FXML private WebView reportWebView;

    private LabOrderEntity selectedOrder;

    public LabViewController(LabDiagnosticsService labDiagnosticsService,
                             LabOrderRepository labOrderRepository,
                             ReportPrintingService reportPrintingService) {
        this.labDiagnosticsService = labDiagnosticsService;
        this.labOrderRepository = labOrderRepository;
        this.reportPrintingService = reportPrintingService;
    }

    @FXML
    public void initialize() {
        if (statusCombo != null) {
            statusCombo.setItems(FXCollections.observableArrayList(
                    "ORDERED", "SAMPLE_COLLECTED", "IN_PROCESS", "COMPLETED", "REVIEWED"
            ));
        }

        if (orderIdCol != null) {
            orderIdCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getOrderId()));
            visitIdCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getVisitId()));
            testNameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTestName()));
            statusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));
            resultCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getResult() != null ? data.getValue().getResult() : "Pending..."));
        }

        if (labOrderTable != null) {
            labOrderTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
                if (newSel != null) {
                    selectedOrder = newSel;
                    if (selectedOrderField != null) selectedOrderField.setText(newSel.getOrderId());
                    if (statusCombo != null) statusCombo.setValue(newSel.getStatus());
                    if (resultArea != null) resultArea.setText(newSel.getResult() != null ? newSel.getResult() : "");
                    renderLabReportPreview(newSel);
                }
            });
        }
        loadAllLabOrders();
    }

    @FXML
    public void handleCreateLabOrder() {
        if (visitIdField == null || visitIdField.getText().isEmpty()) return;
        labDiagnosticsService.createLabOrder(visitIdField.getText(), testNameField.getText());
        loadAllLabOrders();
        visitIdField.clear();
        testNameField.clear();
    }

    @FXML
    public void handleSaveResult() {
        if (selectedOrder == null) return;
        String newStatus = statusCombo != null && statusCombo.getValue() != null ? statusCombo.getValue() : "COMPLETED";
        String findings = resultArea != null ? resultArea.getText() : "";
        labDiagnosticsService.updateLabStatus(selectedOrder.getOrderId(), newStatus, findings);
        loadAllLabOrders();
    }

    @FXML
    public void handlePrintReport() {
        if (selectedOrder != null && reportWebView != null) {
            reportWebView.getEngine().print(null);
        }
    }

    @FXML
    public void loadAllLabOrders() {
        if (labOrderTable != null) {
            List<LabOrderEntity> orders = labOrderRepository.findAll();
            labOrderTable.setItems(FXCollections.observableArrayList(orders));
            if (!orders.isEmpty()) {
                labOrderTable.getSelectionModel().selectFirst();
            }
        }
    }

    private void renderLabReportPreview(LabOrderEntity order) {
        if (reportWebView != null && order != null) {
            String html = reportPrintingService.generateLabReportHtml(order);
            reportWebView.getEngine().loadContent(html);
        }
    }
}
