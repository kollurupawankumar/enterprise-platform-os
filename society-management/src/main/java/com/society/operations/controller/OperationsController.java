package com.society.operations.controller;

import com.society.common.controller.BaseController;
import com.society.common.navigation.NavigationManager;
import com.society.operations.entity.AssetEntity;
import com.society.operations.entity.VendorEntity;
import com.society.operations.repository.AssetRepository;
import com.society.operations.repository.VendorRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OperationsController extends BaseController {

    private final AssetRepository assetRepository;
    private final VendorRepository vendorRepository;

    @FXML
    private TableView<AssetEntity> assetTable;

    @FXML
    private TableColumn<AssetEntity, String> assetNameCol;

    @FXML
    private TableColumn<AssetEntity, String> assetCategoryCol;

    @FXML
    private TableColumn<AssetEntity, String> assetStatusCol;

    @FXML
    private Label serialLabel;

    @FXML
    private Label purchaseDateLabel;

    @FXML
    private Label purchaseCostLabel;

    @FXML
    private Label warrantyLabel;

    @FXML
    private TableView<VendorEntity> vendorTable;

    @FXML
    private TableColumn<VendorEntity, String> vendorNameCol;

    @FXML
    private TableColumn<VendorEntity, String> vendorCategoryCol;

    @FXML
    private TableColumn<VendorEntity, String> vendorPhoneCol;

    @FXML
    private Label contactPersonLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label addressLabel;

    private final ObservableList<AssetEntity> assets = FXCollections.observableArrayList();
    private final ObservableList<VendorEntity> vendors = FXCollections.observableArrayList();

    public OperationsController(
            NavigationManager navigationManager,
            AssetRepository assetRepository,
            VendorRepository vendorRepository) {
        super(navigationManager);
        this.assetRepository = assetRepository;
        this.vendorRepository = vendorRepository;
    }

    @FXML
    public void initialize() {
        // Asset Table Columns
        assetNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        assetCategoryCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategory()));
        assetStatusCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus()));

        assetTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            showAssetDetails(newVal);
        });

        // Vendor Table Columns
        vendorNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        vendorCategoryCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategory()));
        vendorPhoneCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPhone()));

        vendorTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            showVendorDetails(newVal);
        });

        loadAssets();
        loadVendors();
    }

    private void loadAssets() {
        assets.setAll(assetRepository.findAll());
        assetTable.setItems(assets);
    }

    private void loadVendors() {
        vendors.setAll(vendorRepository.findAll());
        vendorTable.setItems(vendors);
    }

    private void showAssetDetails(AssetEntity asset) {
        if (asset == null) {
            serialLabel.setText("-");
            purchaseDateLabel.setText("-");
            purchaseCostLabel.setText("-");
            warrantyLabel.setText("-");
            return;
        }
        serialLabel.setText(asset.getSerialNumber() != null ? asset.getSerialNumber() : "-");
        purchaseDateLabel.setText(asset.getPurchaseDate() != null ? asset.getPurchaseDate() : "-");
        purchaseCostLabel.setText(asset.getPurchaseCost() != null ? "$" + asset.getPurchaseCost() : "-");
        warrantyLabel.setText(asset.getWarrantyExpiryDate() != null ? asset.getWarrantyExpiryDate() : "-");
    }

    private void showVendorDetails(VendorEntity vendor) {
        if (vendor == null) {
            contactPersonLabel.setText("-");
            emailLabel.setText("-");
            addressLabel.setText("-");
            return;
        }
        contactPersonLabel.setText(vendor.getContactPerson() != null ? vendor.getContactPerson() : "-");
        emailLabel.setText(vendor.getEmail() != null ? vendor.getEmail() : "-");
        addressLabel.setText(vendor.getAddress() != null ? vendor.getAddress() : "-");
    }

    @FXML
    private void handleAddAsset() {
        Dialog<AssetEntity> dialog = new Dialog<>();
        dialog.setTitle("Add New Asset");
        dialog.setHeaderText("Enter asset specs:");

        ButtonType addBtnType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addBtnType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        TextField catField = new TextField();
        catField.setPromptText("e.g. Elevator");
        TextField serialField = new TextField();
        TextField costField = new TextField();
        TextField dateField = new TextField();
        dateField.setPromptText("YYYY-MM-DD");

        grid.add(new Label("Asset Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Category:"), 0, 1);
        grid.add(catField, 1, 1);
        grid.add(new Label("Serial No:"), 0, 2);
        grid.add(serialField, 1, 2);
        grid.add(new Label("Purchase Cost:"), 0, 3);
        grid.add(costField, 1, 3);
        grid.add(new Label("Purchase Date:"), 0, 4);
        grid.add(dateField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addBtnType) {
                AssetEntity a = new AssetEntity();
                a.setName(nameField.getText().trim());
                a.setCategory(catField.getText().trim());
                a.setSerialNumber(serialField.getText().trim());
                a.setPurchaseDate(dateField.getText().trim());
                try {
                    a.setPurchaseCost(Double.parseDouble(costField.getText().trim()));
                } catch (NumberFormatException ignored) {}
                a.setStatus("OPERATIONAL");
                return a;
            }
            return null;
        });

        Optional<AssetEntity> result = dialog.showAndWait();
        result.ifPresent(a -> {
            if (!a.getName().isEmpty() && !a.getCategory().isEmpty()) {
                assetRepository.save(a);
                loadAssets();
            }
        });
    }

    @FXML
    private void handleAddVendor() {
        Dialog<VendorEntity> dialog = new Dialog<>();
        dialog.setTitle("Add New Vendor");
        dialog.setHeaderText("Enter vendor contact profile:");

        ButtonType addBtnType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addBtnType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        TextField catField = new TextField();
        catField.setPromptText("e.g. Plumbing");
        TextField contactField = new TextField();
        TextField phoneField = new TextField();
        TextField emailField = new TextField();

        grid.add(new Label("Vendor Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Category:"), 0, 1);
        grid.add(catField, 1, 1);
        grid.add(new Label("Contact Person:"), 0, 2);
        grid.add(contactField, 1, 2);
        grid.add(new Label("Phone:"), 0, 3);
        grid.add(phoneField, 1, 3);
        grid.add(new Label("Email:"), 0, 4);
        grid.add(emailField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addBtnType) {
                VendorEntity v = new VendorEntity();
                v.setName(nameField.getText().trim());
                v.setCategory(catField.getText().trim());
                v.setContactPerson(contactField.getText().trim());
                v.setPhone(phoneField.getText().trim());
                v.setEmail(emailField.getText().trim());
                v.setActive(true);
                return v;
            }
            return null;
        });

        Optional<VendorEntity> result = dialog.showAndWait();
        result.ifPresent(v -> {
            if (!v.getName().isEmpty() && !v.getCategory().isEmpty()) {
                vendorRepository.save(v);
                loadVendors();
            }
        });
    }
}
