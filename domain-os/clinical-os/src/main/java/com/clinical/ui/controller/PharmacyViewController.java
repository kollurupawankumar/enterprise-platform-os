package com.clinical.ui.controller;

import com.clinical.pharmacy.entity.MedicineInventoryEntity;
import com.clinical.pharmacy.service.PharmacyService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PharmacyViewController {

    private final PharmacyService pharmacyService;

    @FXML private TableView<MedicineInventoryEntity> inventoryTable;
    @FXML private TableColumn<MedicineInventoryEntity, String> nameCol;
    @FXML private TableColumn<MedicineInventoryEntity, String> batchCol;
    @FXML private TableColumn<MedicineInventoryEntity, Integer> qtyCol;
    @FXML private TableColumn<MedicineInventoryEntity, BigDecimal> priceCol;

    @FXML private TextField medicineNameField;
    @FXML private TextField batchNumberField;
    @FXML private TextField quantityField;
    @FXML private TextField priceField;

    public PharmacyViewController(PharmacyService pharmacyService) {
        this.pharmacyService = pharmacyService;
    }

    @FXML
    public void initialize() {
        if (nameCol != null) {
            nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMedicineName()));
            batchCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBatchNumber()));
            qtyCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getQuantity()));
            priceCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getSellingPrice()));
        }
    }

    @FXML
    public void handleAddStock() {
        if (medicineNameField == null || medicineNameField.getText().isEmpty()) return;
        MedicineInventoryEntity med = new MedicineInventoryEntity();
        med.setMedicineName(medicineNameField.getText());
        med.setBatchNumber(batchNumberField.getText());
        med.setQuantity(Integer.parseInt(quantityField.getText()));
        med.setSellingPrice(new BigDecimal(priceField.getText()));
        pharmacyService.addOrUpdateStock(med);
        medicineNameField.clear();
        batchNumberField.clear();
        quantityField.clear();
        priceField.clear();
    }
}
