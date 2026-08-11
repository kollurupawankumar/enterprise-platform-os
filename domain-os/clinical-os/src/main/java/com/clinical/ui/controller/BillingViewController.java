package com.clinical.ui.controller;

import com.clinical.billing.service.BillingService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class BillingViewController {

    private final BillingService billingService;

    @FXML private TextField visitIdField;
    @FXML private TextField consultationFeeField;
    @FXML private TextField labFeeField;
    @FXML private TextField pharmacyFeeField;
    @FXML private ComboBox<String> paymentModeCombo;

    public BillingViewController(BillingService billingService) {
        this.billingService = billingService;
    }

    @FXML
    public void initialize() {
        if (paymentModeCombo != null) {
            paymentModeCombo.setItems(FXCollections.observableArrayList("CASH", "UPI", "CARD", "BANK_TRANSFER"));
        }
    }

    @FXML
    public void handleGenerateInvoice() {
        if (visitIdField == null || visitIdField.getText().isEmpty()) return;
        billingService.generateInvoice(
                visitIdField.getText(),
                "PAT-000001",
                new BigDecimal(consultationFeeField.getText()),
                new BigDecimal(labFeeField.getText()),
                new BigDecimal(pharmacyFeeField.getText())
        );
    }
}
