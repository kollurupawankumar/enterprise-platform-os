package com.society.operations.controller;

import com.society.common.controller.BaseController;
import com.society.common.navigation.NavigationManager;
import com.society.common.service.FileStorageService;
import com.society.operations.entity.AssetEntity;
import com.society.operations.entity.FacilityEntity;
import com.society.operations.entity.SocietyStaffEntity;
import com.society.operations.entity.VendorEntity;
import com.society.operations.service.OperationsService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Optional;

@Component
public class OperationsController extends BaseController {

    private final OperationsService operationsService;
    private final FileStorageService fileStorageService;

    // Assets
    @FXML private TableView<AssetEntity> assetTable;
    @FXML private TableColumn<AssetEntity, String> assetNameCol;
    @FXML private TableColumn<AssetEntity, String> assetCategoryCol;
    @FXML private TableColumn<AssetEntity, String> assetStatusCol;
    @FXML private Label serialLabel;
    @FXML private Label purchaseDateLabel;
    @FXML private Label purchaseCostLabel;
    @FXML private Label warrantyLabel;

    // Vendors
    @FXML private TableView<VendorEntity> vendorTable;
    @FXML private TableColumn<VendorEntity, String> vendorNameCol;
    @FXML private TableColumn<VendorEntity, String> vendorCategoryCol;
    @FXML private TableColumn<VendorEntity, String> vendorPhoneCol;
    @FXML private Label contactPersonLabel;
    @FXML private Label emailLabel;
    @FXML private Label addressLabel;

    // Facilities
    @FXML private TableView<FacilityEntity> facilityTable;
    @FXML private TableColumn<FacilityEntity, String> facilityNameCol;
    @FXML private TableColumn<FacilityEntity, String> facilityTypeCol;
    @FXML private TableColumn<FacilityEntity, String> facilityLocationCol;
    @FXML private TableColumn<FacilityEntity, Number> facilityCapacityCol;
    @FXML private TableColumn<FacilityEntity, String> facilityTimingsCol;
    @FXML private TableColumn<FacilityEntity, String> facilityStatusCol;

    // Staff & Police Verification
    @FXML private TableView<SocietyStaffEntity> staffTable;
    @FXML private TableColumn<SocietyStaffEntity, String> staffNameCol;
    @FXML private TableColumn<SocietyStaffEntity, String> staffRoleCol;
    @FXML private TableColumn<SocietyStaffEntity, String> staffMobileCol;
    @FXML private TableColumn<SocietyStaffEntity, String> staffShiftCol;
    @FXML private TableColumn<SocietyStaffEntity, String> staffPoliceStatusCol;
    @FXML private TableColumn<SocietyStaffEntity, String> staffDocPathCol;

    private final ObservableList<AssetEntity> assets = FXCollections.observableArrayList();
    private final ObservableList<VendorEntity> vendors = FXCollections.observableArrayList();
    private final ObservableList<FacilityEntity> facilities = FXCollections.observableArrayList();
    private final ObservableList<SocietyStaffEntity> staffMembers = FXCollections.observableArrayList();

    public OperationsController(
            NavigationManager navigationManager,
            OperationsService operationsService,
            FileStorageService fileStorageService) {
        super(navigationManager);
        this.operationsService = operationsService;
        this.fileStorageService = fileStorageService;
    }

    @FXML
    public void initialize() {
        // Asset column mapping
        assetNameCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));
        assetCategoryCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCategory()));
        assetStatusCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStatus()));

        assetTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            showAssetDetails(newVal);
        });

        // Vendor column mapping
        vendorNameCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));
        vendorCategoryCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCategory()));
        vendorPhoneCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPhone()));

        vendorTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            showVendorDetails(newVal);
        });

        // Facility column mapping
        facilityNameCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));
        facilityTypeCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getType()));
        facilityLocationCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getLocation()));
        facilityCapacityCol.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getCapacity() != null ? cell.getValue().getCapacity() : 0));
        facilityTimingsCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTimings()));
        facilityStatusCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStatus()));

        // Staff column mapping
        staffNameCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));
        staffRoleCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getRole()));
        staffMobileCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getMobileNumber()));
        staffShiftCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getShiftTiming()));
        staffPoliceStatusCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPoliceVerificationStatus()));
        staffDocPathCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPoliceDocPath() != null ? cell.getValue().getPoliceDocPath() : "-"));

        loadAssets();
        loadVendors();
        loadFacilities();
        loadStaff();
    }

    private void loadAssets() {
        assets.setAll(operationsService.getAllAssets());
        assetTable.setItems(assets);
    }

    private void loadVendors() {
        vendors.setAll(operationsService.getAllVendors());
        vendorTable.setItems(vendors);
    }

    private void loadFacilities() {
        facilities.setAll(operationsService.getAllFacilities());
        facilityTable.setItems(facilities);
    }

    private void loadStaff() {
        staffMembers.setAll(operationsService.getAllStaff());
        staffTable.setItems(staffMembers);
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
        purchaseCostLabel.setText(asset.getPurchaseCost() != null ? "₹ " + asset.getPurchaseCost() : "-");
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
        dialog.setTitle("Add Asset");
        dialog.setHeaderText("Enter Asset Details:");

        ButtonType saveBtnType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtnType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField(); nameField.setPromptText("Asset Name");
        TextField catField = new TextField(); catField.setPromptText("Category (e.g. LIFT, DG)");
        TextField serialField = new TextField(); serialField.setPromptText("Serial Number");

        grid.add(new Label("Name:"), 0, 0); grid.add(nameField, 1, 0);
        grid.add(new Label("Category:"), 0, 1); grid.add(catField, 1, 1);
        grid.add(new Label("Serial No:"), 0, 2); grid.add(serialField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveBtnType) {
                AssetEntity a = new AssetEntity();
                a.setName(nameField.getText().trim());
                a.setCategory(catField.getText().trim());
                a.setSerialNumber(serialField.getText().trim());
                a.setStatus("OPERATIONAL");
                return a;
            }
            return null;
        });

        Optional<AssetEntity> result = dialog.showAndWait();
        result.ifPresent(a -> {
            if (!a.getName().isEmpty()) {
                operationsService.saveAsset(a);
                loadAssets();
            }
        });
    }

    @FXML
    private void handleAddVendor() {
        Dialog<VendorEntity> dialog = new Dialog<>();
        dialog.setTitle("Add Vendor");
        dialog.setHeaderText("Enter Vendor Directory Details:");

        ButtonType saveBtnType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtnType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField(); nameField.setPromptText("Vendor Company Name");
        TextField catField = new TextField(); catField.setPromptText("Category (e.g. SECURITY, LIFT)");
        TextField phoneField = new TextField(); phoneField.setPromptText("Phone Number");

        grid.add(new Label("Name:"), 0, 0); grid.add(nameField, 1, 0);
        grid.add(new Label("Category:"), 0, 1); grid.add(catField, 1, 1);
        grid.add(new Label("Phone:"), 0, 2); grid.add(phoneField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveBtnType) {
                VendorEntity v = new VendorEntity();
                v.setName(nameField.getText().trim());
                v.setCategory(catField.getText().trim());
                v.setPhone(phoneField.getText().trim());
                return v;
            }
            return null;
        });

        Optional<VendorEntity> result = dialog.showAndWait();
        result.ifPresent(v -> {
            if (!v.getName().isEmpty()) {
                operationsService.saveVendor(v);
                loadVendors();
            }
        });
    }

    @FXML
    private void handleAddFacility() {
        Dialog<FacilityEntity> dialog = new Dialog<>();
        dialog.setTitle("Add Society Facility");
        dialog.setHeaderText("Specify Facility Details:");

        ButtonType saveBtnType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtnType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField(); nameField.setPromptText("e.g. Clubhouse Hall");
        ComboBox<String> typeCombo = new ComboBox<>(FXCollections.observableArrayList("CLUBHOUSE", "GYM", "SWIMMING_POOL", "TENNIS_COURT", "GUEST_ROOM"));
        typeCombo.getSelectionModel().selectFirst();
        TextField locField = new TextField(); locField.setPromptText("e.g. Wing A Ground Floor");
        TextField capField = new TextField(); capField.setPromptText("e.g. 100");
        TextField timingsField = new TextField(); timingsField.setPromptText("e.g. 06:00 AM - 10:00 PM");

        grid.add(new Label("Facility Name:"), 0, 0); grid.add(nameField, 1, 0);
        grid.add(new Label("Type:"), 0, 1); grid.add(typeCombo, 1, 1);
        grid.add(new Label("Location:"), 0, 2); grid.add(locField, 1, 2);
        grid.add(new Label("Capacity:"), 0, 3); grid.add(capField, 1, 3);
        grid.add(new Label("Timings:"), 0, 4); grid.add(timingsField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveBtnType) {
                FacilityEntity f = new FacilityEntity();
                f.setName(nameField.getText().trim());
                f.setType(typeCombo.getValue());
                f.setLocation(locField.getText().trim());
                try { f.setCapacity(Integer.parseInt(capField.getText().trim())); } catch (Exception ignored) {}
                f.setTimings(timingsField.getText().trim());
                f.setStatus("OPERATIONAL");
                return f;
            }
            return null;
        });

        Optional<FacilityEntity> result = dialog.showAndWait();
        result.ifPresent(f -> {
            if (!f.getName().isEmpty()) {
                operationsService.saveFacility(f);
                loadFacilities();
            }
        });
    }

    @FXML
    private void handleAddStaff() {
        Dialog<SocietyStaffEntity> dialog = new Dialog<>();
        dialog.setTitle("Add Staff & Police Verification");
        dialog.setHeaderText("Enter Staff Member & Verification details:");

        ButtonType saveBtnType = new ButtonType("Add Staff", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtnType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField(); nameField.setPromptText("Staff Full Name");
        ComboBox<String> roleCombo = new ComboBox<>(FXCollections.observableArrayList(
                "SECURITY", "HOUSEKEEPING", "ESTATE_MANAGER", "ELECTRICIAN", "PLUMBER"
        ));
        roleCombo.getSelectionModel().selectFirst();
        TextField mobileField = new TextField(); mobileField.setPromptText("Mobile Number");
        TextField shiftField = new TextField(); shiftField.setPromptText("e.g. Day Shift (08:00 - 20:00)");

        ComboBox<String> policeCombo = new ComboBox<>(FXCollections.observableArrayList("VERIFIED", "PENDING", "REJECTED"));
        policeCombo.getSelectionModel().select("PENDING");

        TextField docPathField = new TextField(); docPathField.setPromptText("Relative doc path..."); docPathField.setEditable(false);
        Button browseBtn = new Button("Browse");

        browseBtn.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Select Police Verification File");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF or Images", "*.pdf", "*.jpg", "*.png"));
            File file = chooser.showOpenDialog(dialog.getOwner());
            if (file != null) {
                try {
                    String relPath = fileStorageService.storeFile(file, "staff");
                    docPathField.setText(relPath);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        HBox fileBox = new HBox(10, docPathField, browseBtn);

        grid.add(new Label("Staff Name:"), 0, 0); grid.add(nameField, 1, 0);
        grid.add(new Label("Role:"), 0, 1); grid.add(roleCombo, 1, 1);
        grid.add(new Label("Mobile:"), 0, 2); grid.add(mobileField, 1, 2);
        grid.add(new Label("Shift Timing:"), 0, 3); grid.add(shiftField, 1, 3);
        grid.add(new Label("Police Status:"), 0, 4); grid.add(policeCombo, 1, 4);
        grid.add(new Label("Verification Doc:"), 0, 5); grid.add(fileBox, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveBtnType) {
                SocietyStaffEntity s = new SocietyStaffEntity();
                s.setName(nameField.getText().trim());
                s.setRole(roleCombo.getValue());
                s.setMobileNumber(mobileField.getText().trim());
                s.setShiftTiming(shiftField.getText().trim());
                s.setPoliceVerificationStatus(policeCombo.getValue());
                s.setPoliceDocPath(docPathField.getText().trim());
                s.setActive(true);
                return s;
            }
            return null;
        });

        Optional<SocietyStaffEntity> result = dialog.showAndWait();
        result.ifPresent(s -> {
            if (!s.getName().isEmpty()) {
                operationsService.saveStaff(s);
                loadStaff();
            }
        });
    }
}
