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
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
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

    // Service Log Controls
    @FXML private TableView<com.society.operations.entity.AssetServiceLogEntity> serviceLogTable;
    @FXML private TableColumn<com.society.operations.entity.AssetServiceLogEntity, String> serviceDateCol;
    @FXML private TableColumn<com.society.operations.entity.AssetServiceLogEntity, String> serviceTypeCol;
    @FXML private TableColumn<com.society.operations.entity.AssetServiceLogEntity, String> serviceEngCol;
    @FXML private TableColumn<com.society.operations.entity.AssetServiceLogEntity, String> serviceSummaryCol;
    @FXML private TableColumn<com.society.operations.entity.AssetServiceLogEntity, String> serviceDocCol;
    private final ObservableList<com.society.operations.entity.AssetServiceLogEntity> serviceLogs = FXCollections.observableArrayList();

    // Vendors
    @FXML private TableView<VendorEntity> vendorTable;
    @FXML private TableColumn<VendorEntity, String> vendorNameCol;
    @FXML private TableColumn<VendorEntity, String> vendorCategoryCol;
    @FXML private TableColumn<VendorEntity, String> vendorPhoneCol;
    @FXML private TableColumn<VendorEntity, String> vendorStatusCol;
    @FXML private Label vendorNameVal;
    @FXML private Label vendorCategoryVal;
    @FXML private Label vendorPhoneVal;
    @FXML private Label contactPersonLabel;
    @FXML private Label emailLabel;
    @FXML private Label addressLabel;
    @FXML private Label vendorStatusVal;
    @FXML private ListView<String> vendorAssetList;

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
    @FXML private TableColumn<SocietyStaffEntity, String> staffStatusCol;

    private final ObservableList<AssetEntity> assets = FXCollections.observableArrayList();
    private final ObservableList<VendorEntity> vendors = FXCollections.observableArrayList();
    private final ObservableList<FacilityEntity> facilities = FXCollections.observableArrayList();
    private final ObservableList<SocietyStaffEntity> staffMembers = FXCollections.observableArrayList();

    @FXML private Button addAssetBtn;
    @FXML private Button editAssetBtn;
    @FXML private Button logServiceVisitBtn;
    @FXML private Button addVendorBtn;
    @FXML private Button editVendorBtn;
    @FXML private Button addFacilityBtn;
    @FXML private Button addStaffBtn;
    @FXML private Button deactivateStaffBtn;

    private final com.society.user.context.UserContext userContext;

    public OperationsController(
            NavigationManager navigationManager,
            OperationsService operationsService,
            FileStorageService fileStorageService,
            com.society.user.context.UserContext userContext) {
        super(navigationManager);
        this.operationsService = operationsService;
        this.fileStorageService = fileStorageService;
        this.userContext = userContext;
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

        // Service log column mapping
        serviceDateCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getServiceDate()));
        serviceTypeCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getServiceType()));
        serviceEngCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEngineerName() != null ? cell.getValue().getEngineerName() : "-"));
        serviceSummaryCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getWorkSummary() != null ? cell.getValue().getWorkSummary() : "-"));
        serviceDocCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDocumentPath() != null ? cell.getValue().getDocumentPath() : "No Document"));

        // Vendor column mapping
        vendorNameCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));
        vendorCategoryCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCategory()));
        vendorPhoneCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPhone()));
        vendorStatusCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().isActive() ? "ACTIVE" : "INACTIVE"));

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
        staffStatusCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().isActive() ? "ACTIVE" : "INACTIVE"));

        loadAssets();
        loadVendors();
        loadFacilities();
        loadStaff();
        applyRolePermissions();
    }

    private void applyRolePermissions() {
        boolean canEdit = userContext.canEdit("OPERATIONS");
        if (addAssetBtn != null) { addAssetBtn.setVisible(canEdit); addAssetBtn.setManaged(canEdit); }
        if (editAssetBtn != null) { editAssetBtn.setVisible(canEdit); editAssetBtn.setManaged(canEdit); }
        if (logServiceVisitBtn != null) { logServiceVisitBtn.setVisible(canEdit); logServiceVisitBtn.setManaged(canEdit); }
        if (addVendorBtn != null) { addVendorBtn.setVisible(canEdit); addVendorBtn.setManaged(canEdit); }
        if (editVendorBtn != null) { editVendorBtn.setVisible(canEdit); editVendorBtn.setManaged(canEdit); }
        if (addFacilityBtn != null) { addFacilityBtn.setVisible(canEdit); addFacilityBtn.setManaged(canEdit); }
        if (addStaffBtn != null) { addStaffBtn.setVisible(canEdit); addStaffBtn.setManaged(canEdit); }
        if (deactivateStaffBtn != null) { deactivateStaffBtn.setVisible(canEdit); deactivateStaffBtn.setManaged(canEdit); }
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

    @FXML private Label amcVendorLabel;
    @FXML private Label amcPeriodLabel;
    @FXML private Label amcCostLabel;
    @FXML private Label amcDetailsLabel;

    private AssetEntity selectedAsset;

    private void showAssetDetails(AssetEntity asset) {
        this.selectedAsset = asset;
        if (asset == null) {
            serialLabel.setText("-");
            purchaseDateLabel.setText("-");
            purchaseCostLabel.setText("-");
            warrantyLabel.setText("-");
            amcVendorLabel.setText("-");
            amcPeriodLabel.setText("-");
            amcCostLabel.setText("-");
            amcDetailsLabel.setText("-");
            return;
        }
        serialLabel.setText(asset.getSerialNumber() != null && !asset.getSerialNumber().isBlank() ? asset.getSerialNumber() : "-");
        purchaseDateLabel.setText(asset.getPurchaseDate() != null && !asset.getPurchaseDate().isBlank() ? asset.getPurchaseDate() : "-");
        purchaseCostLabel.setText(asset.getPurchaseCost() != null ? "₹ " + String.format("%.2f", asset.getPurchaseCost()) : "-");
        warrantyLabel.setText(asset.getWarrantyExpiryDate() != null && !asset.getWarrantyExpiryDate().isBlank() ? asset.getWarrantyExpiryDate() : "-");

        VendorEntity v = asset.getAmcVendor();
        amcVendorLabel.setText(v != null ? v.getName() + " (" + (v.getPhone() != null ? v.getPhone() : "") + ")" : "No AMC Vendor Assigned");
        
        String start = asset.getAmcStartDate() != null ? asset.getAmcStartDate() : "";
        String expiry = asset.getAmcExpiryDate() != null ? asset.getAmcExpiryDate() : "";
        if (!start.isEmpty() || !expiry.isEmpty()) {
            amcPeriodLabel.setText(start + " to " + (expiry.isEmpty() ? "N/A" : expiry));
        } else {
            amcPeriodLabel.setText("-");
        }

        amcCostLabel.setText(asset.getAmcCost() != null ? "₹ " + String.format("%.2f", asset.getAmcCost()) : "-");
        amcDetailsLabel.setText(asset.getAmcDetails() != null && !asset.getAmcDetails().isBlank() ? asset.getAmcDetails() : "No details.");

        loadServiceLogs(asset.getId());
    }

    private void loadServiceLogs(Integer assetId) {
        if (assetId == null) {
            serviceLogs.clear();
        } else {
            serviceLogs.setAll(operationsService.getServiceLogsForAsset(assetId));
        }
        serviceLogTable.setItems(serviceLogs);
    }

    @FXML
    private void handleLogServiceVisit() {
        if (selectedAsset == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Selection Required");
            alert.setHeaderText(null);
            alert.setContentText("Please select an asset first before logging a service visit.");
            alert.showAndWait();
            return;
        }

        Dialog<com.society.operations.entity.AssetServiceLogEntity> dialog = new Dialog<>();
        dialog.setTitle("Log AMC / Repair Service Visit");
        dialog.setHeaderText("Record service visit details for: " + selectedAsset.getName());

        ButtonType saveBtnType = new ButtonType("Save Log", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtnType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(15));

        DatePicker serviceDatePicker = new DatePicker(LocalDate.now());
        ComboBox<String> typeCombo = new ComboBox<>(FXCollections.observableArrayList("ROUTINE_AMC", "REPAIR", "INSPECTION", "EMERGENCY"));
        typeCombo.getSelectionModel().selectFirst();

        TextField engField = new TextField();
        engField.setPromptText("Service Engineer / Vendor Contact Name");

        TextField costField = new TextField();
        costField.setPromptText("Cost incurred ₹ (0 if covered under AMC)");

        TextArea summaryArea = new TextArea();
        summaryArea.setPromptText("Work performed, parts replaced, remarks...");
        summaryArea.setPrefRowCount(3);

        Label docPathLabel = new Label("No document selected");
        Button uploadBtn = new Button("Upload Service Receipt / Photo(s)");
        final String[] uploadedPath = new String[1];

        uploadBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Service Receipt / Photo(s)");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Images & Documents", "*.jpg", "*.jpeg", "*.png", "*.pdf")
            );
            List<File> files = fileChooser.showOpenMultipleDialog(serviceLogTable.getScene().getWindow());
            if (files != null && !files.isEmpty()) {
                try {
                    List<String> paths = new java.util.ArrayList<>();
                    for (File f : files) {
                        paths.add(fileStorageService.storeFile(f, "assets/service_receipts"));
                    }
                    uploadedPath[0] = String.join(";", paths);
                    docPathLabel.setText(files.size() + " document(s) attached");
                } catch (Exception ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Failed to upload file: " + ex.getMessage());
                    alert.showAndWait();
                }
            }
        });

        grid.add(new Label("Service Date:"), 0, 0); grid.add(serviceDatePicker, 1, 0);
        grid.add(new Label("Service Type:"), 0, 1); grid.add(typeCombo, 1, 1);
        grid.add(new Label("Engineer / Contact:"), 0, 2); grid.add(engField, 1, 2);
        grid.add(new Label("Cost (₹):"), 0, 3); grid.add(costField, 1, 3);
        grid.add(new Label("Work Summary:"), 0, 4); grid.add(summaryArea, 1, 4);
        grid.add(new Label("Inspection Photos / Bills:"), 0, 5); grid.add(new HBox(10, uploadBtn, docPathLabel), 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtnType) {
                com.society.operations.entity.AssetServiceLogEntity log = new com.society.operations.entity.AssetServiceLogEntity();
                log.setAsset(selectedAsset);
                log.setServiceDate(serviceDatePicker.getValue() != null ? serviceDatePicker.getValue().toString() : LocalDate.now().toString());
                log.setServiceType(typeCombo.getValue());
                log.setEngineerName(engField.getText().trim());
                try { log.setCost(Double.parseDouble(costField.getText().trim())); } catch (Exception ignored) {}
                log.setWorkSummary(summaryArea.getText().trim());
                log.setDocumentPath(uploadedPath[0]);

                String currentUser = userContext.isLoggedIn() ? userContext.getCurrentUser().getUsername() : "SYSTEM";
                log.setCreatedBy(currentUser);
                log.setUpdatedBy(currentUser);
                return log;
            }
            return null;
        });

        Optional<com.society.operations.entity.AssetServiceLogEntity> result = dialog.showAndWait();
        result.ifPresent(log -> {
            operationsService.logServiceVisit(log);
            loadServiceLogs(selectedAsset.getId());
        });
    }

    @FXML
    private void handleServiceLogClicked() {
        com.society.operations.entity.AssetServiceLogEntity selectedLog = serviceLogTable.getSelectionModel().getSelectedItem();
        if (selectedLog == null) {
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Service Visit & Photo Inspection Details");
        dialog.setHeaderText("Asset: " + (selectedAsset != null ? selectedAsset.getName() : "N/A"));
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        VBox contentBox = new VBox(15);
        contentBox.setPadding(new Insets(15));
        contentBox.setPrefWidth(550);

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(8);

        grid.add(new Label("Service Date:"), 0, 0); grid.add(new Label(selectedLog.getServiceDate()), 1, 0);
        grid.add(new Label("Service Type:"), 0, 1); grid.add(new Label(selectedLog.getServiceType()), 1, 1);
        grid.add(new Label("Engineer / Contact:"), 0, 2); grid.add(new Label(selectedLog.getEngineerName() != null ? selectedLog.getEngineerName() : "-"), 1, 2);
        grid.add(new Label("Cost Incurred:"), 0, 3); grid.add(new Label(com.society.common.util.CurrencyUtils.formatInr(selectedLog.getCost() != null ? selectedLog.getCost() : 0.0)), 1, 3);
        grid.add(new Label("Work Performed:"), 0, 4); grid.add(new Label(selectedLog.getWorkSummary() != null ? selectedLog.getWorkSummary() : "-"), 1, 4);

        contentBox.getChildren().add(grid);

        // Uploaded Photos & Documents Section
        Label docHeader = new Label("Attached Inspection Photos & Bills:");
        docHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        contentBox.getChildren().add(docHeader);

        String docPath = selectedLog.getDocumentPath();
        if (docPath != null && !docPath.isBlank()) {
            String[] paths = docPath.split(";");
            FlowPane mediaPane = new FlowPane();
            mediaPane.setHgap(10); mediaPane.setVgap(10);

            for (String p : paths) {
                String cleanPath = p.trim();
                File file = fileStorageService.getFileByPath(cleanPath);
                if (file != null && file.exists()) {
                    String lowerName = file.getName().toLowerCase();
                    if (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg") || lowerName.endsWith(".png")) {
                        try {
                            javafx.scene.image.ImageView imgView = new javafx.scene.image.ImageView(new javafx.scene.image.Image(file.toURI().toString()));
                            imgView.setFitWidth(150);
                            imgView.setPreserveRatio(true);
                            imgView.setStyle("-fx-border-color: #CBD5E1; -fx-border-width: 1; -fx-cursor: hand;");
                            
                            // Double-click or click to open full file
                            imgView.setOnMouseClicked(e -> {
                                try { java.awt.Desktop.getDesktop().open(file); } catch (Exception ignored) {}
                            });
                            mediaPane.getChildren().add(imgView);
                        } catch (Exception ex) {
                            Hyperlink link = new Hyperlink(file.getName());
                            link.setOnAction(e -> { try { java.awt.Desktop.getDesktop().open(file); } catch (Exception ignored) {} });
                            mediaPane.getChildren().add(link);
                        }
                    } else {
                        Hyperlink link = new Hyperlink(file.getName() + " (Open File)");
                        link.setOnAction(e -> { try { java.awt.Desktop.getDesktop().open(file); } catch (Exception ignored) {} });
                        mediaPane.getChildren().add(link);
                    }
                } else {
                    mediaPane.getChildren().add(new Label("File path: " + cleanPath + " (File not found)"));
                }
            }
            contentBox.getChildren().add(mediaPane);
        } else {
            contentBox.getChildren().add(new Label("No inspection photos or receipts attached for this service visit."));
        }

        dialog.getDialogPane().setContent(contentBox);
        dialog.showAndWait();
    }

    private void showVendorDetails(VendorEntity vendor) {
        if (vendor == null) {
            if (vendorNameVal != null) vendorNameVal.setText("-");
            if (vendorCategoryVal != null) vendorCategoryVal.setText("-");
            if (vendorPhoneVal != null) vendorPhoneVal.setText("-");
            if (contactPersonLabel != null) contactPersonLabel.setText("-");
            if (emailLabel != null) emailLabel.setText("-");
            if (addressLabel != null) addressLabel.setText("-");
            if (vendorStatusVal != null) vendorStatusVal.setText("-");
            if (vendorAssetList != null) vendorAssetList.getItems().clear();
            return;
        }
        if (vendorNameVal != null) vendorNameVal.setText(vendor.getName() != null ? vendor.getName() : "-");
        if (vendorCategoryVal != null) vendorCategoryVal.setText(vendor.getCategory() != null ? vendor.getCategory() : "-");
        if (vendorPhoneVal != null) vendorPhoneVal.setText(vendor.getPhone() != null ? vendor.getPhone() : "-");
        if (contactPersonLabel != null) contactPersonLabel.setText(vendor.getContactPerson() != null ? vendor.getContactPerson() : "-");
        if (emailLabel != null) emailLabel.setText(vendor.getEmail() != null ? vendor.getEmail() : "-");
        if (addressLabel != null) addressLabel.setText(vendor.getAddress() != null ? vendor.getAddress() : "-");
        if (vendorStatusVal != null) {
            boolean active = vendor.isActive();
            vendorStatusVal.setText(active ? "ACTIVE" : "INACTIVE");
            vendorStatusVal.setStyle("-fx-text-fill: " + (active ? "#16A34A;" : "#DC2626;"));
        }

        // Query assets assigned to this vendor
        if (vendorAssetList != null) {
            vendorAssetList.getItems().clear();
            List<AssetEntity> allAssets = operationsService.getAllAssets();
            List<String> assignedAssets = allAssets.stream()
                    .filter(a -> a.getAmcVendor() != null && a.getAmcVendor().getId() != null && a.getAmcVendor().getId().equals(vendor.getId()))
                    .map(a -> a.getName() + " (" + (a.getCategory() != null ? a.getCategory() : "ASSET") + ") - AMC: " 
                            + (a.getAmcExpiryDate() != null ? "Exp " + a.getAmcExpiryDate() : "Active"))
                    .collect(java.util.stream.Collectors.toList());

            if (assignedAssets.isEmpty()) {
                vendorAssetList.getItems().add("No AMC contracts currently assigned to this vendor.");
            } else {
                vendorAssetList.getItems().addAll(assignedAssets);
            }
        }
    }

    @FXML
    private void handleAddAsset() {
        showAssetDialog(new AssetEntity());
    }

    @FXML
    private void handleEditAsset() {
        if (selectedAsset == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Selection Required");
            alert.setHeaderText(null);
            alert.setContentText("Please select an asset from the table to edit.");
            alert.showAndWait();
            return;
        }
        showAssetDialog(selectedAsset);
    }

    private void showAssetDialog(AssetEntity asset) {
        boolean isEdit = asset.getId() != null;
        Dialog<AssetEntity> dialog = new Dialog<>();
        dialog.setTitle(isEdit ? "Edit Asset & AMC Details" : "Add Asset & AMC Details");
        dialog.setHeaderText(isEdit ? "Update Asset details:" : "Enter Asset & AMC Registration Details:");

        ButtonType saveBtnType = new ButtonType(isEdit ? "Update" : "Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtnType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(15));

        TextField nameField = new TextField(asset.getName() != null ? asset.getName() : "");
        nameField.setPromptText("Asset Name (e.g. Passenger Lift A)");

        ComboBox<String> categoryCombo = new ComboBox<>(FXCollections.observableArrayList(
                "ELEVATOR", "GENERATOR", "WATER_PUMP", "SECURITY_CAMERAS", "FIRE_EXTINGUISHER", "SOLAR_PANEL", "CLUBHOUSE_EQUIPMENT", "OTHER"
        ));
        categoryCombo.setEditable(true);
        if (asset.getCategory() != null) {
            categoryCombo.getSelectionModel().select(asset.getCategory());
        } else {
            categoryCombo.getSelectionModel().selectFirst();
        }

        TextField serialField = new TextField(asset.getSerialNumber() != null ? asset.getSerialNumber() : "");
        serialField.setPromptText("Serial Number / Tag No");

        DatePicker purchaseDatePicker = new DatePicker(asset.getPurchaseDate() != null && !asset.getPurchaseDate().isBlank() ? LocalDate.parse(asset.getPurchaseDate()) : LocalDate.now());
        TextField costField = new TextField(asset.getPurchaseCost() != null ? String.valueOf(asset.getPurchaseCost()) : "");
        costField.setPromptText("Purchase Cost in ₹");

        DatePicker warrantyDatePicker = new DatePicker(asset.getWarrantyExpiryDate() != null && !asset.getWarrantyExpiryDate().isBlank() ? LocalDate.parse(asset.getWarrantyExpiryDate()) : LocalDate.now().plusYears(1));

        ComboBox<String> statusCombo = new ComboBox<>(FXCollections.observableArrayList("OPERATIONAL", "UNDER_MAINTENANCE", "SCRAPPED"));
        statusCombo.getSelectionModel().select(asset.getStatus() != null ? asset.getStatus() : "OPERATIONAL");

        // AMC Section - filter active vendors (or preserve existing vendor if edit)
        List<VendorEntity> activeVendors = vendors.stream()
                .filter(v -> v.isActive() || (asset.getAmcVendor() != null && asset.getAmcVendor().getId().equals(v.getId())))
                .collect(java.util.stream.Collectors.toList());
        ComboBox<VendorEntity> amcVendorCombo = new ComboBox<>(FXCollections.observableArrayList(activeVendors));
        amcVendorCombo.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(VendorEntity v) {
                return v != null ? v.getName() + " (" + v.getCategory() + ")" + (!v.isActive() ? " [INACTIVE]" : "") : "";
            }
            @Override
            public VendorEntity fromString(String string) {
                return null;
            }
        });
        if (asset.getAmcVendor() != null) {
            amcVendorCombo.getSelectionModel().select(asset.getAmcVendor());
        }

        DatePicker amcStartDatePicker = new DatePicker(asset.getAmcStartDate() != null && !asset.getAmcStartDate().isBlank() ? LocalDate.parse(asset.getAmcStartDate()) : LocalDate.now());
        DatePicker amcExpiryDatePicker = new DatePicker(asset.getAmcExpiryDate() != null && !asset.getAmcExpiryDate().isBlank() ? LocalDate.parse(asset.getAmcExpiryDate()) : LocalDate.now().plusYears(1));
        TextField amcCostField = new TextField(asset.getAmcCost() != null ? String.valueOf(asset.getAmcCost()) : "");
        amcCostField.setPromptText("Annual Contract Cost ₹");

        TextArea amcDetailsArea = new TextArea(asset.getAmcDetails() != null ? asset.getAmcDetails() : "");
        amcDetailsArea.setPromptText("AMC terms / Service frequency / Contact notes...");
        amcDetailsArea.setPrefRowCount(2);

        grid.add(new Label("Asset Name:"), 0, 0); grid.add(nameField, 1, 0);
        grid.add(new Label("Category:"), 0, 1); grid.add(categoryCombo, 1, 1);
        grid.add(new Label("Serial / Tag No:"), 0, 2); grid.add(serialField, 1, 2);
        grid.add(new Label("Purchase Date:"), 0, 3); grid.add(purchaseDatePicker, 1, 3);
        grid.add(new Label("Purchase Cost (₹):"), 0, 4); grid.add(costField, 1, 4);
        grid.add(new Label("Warranty Expiry:"), 0, 5); grid.add(warrantyDatePicker, 1, 5);
        grid.add(new Label("Status:"), 0, 6); grid.add(statusCombo, 1, 6);

        grid.add(new Label("--- AMC Details ---"), 0, 7, 2, 1);
        grid.add(new Label("AMC Vendor:"), 0, 8); grid.add(amcVendorCombo, 1, 8);
        grid.add(new Label("AMC Start Date:"), 0, 9); grid.add(amcStartDatePicker, 1, 9);
        grid.add(new Label("AMC Expiry Date:"), 0, 10); grid.add(amcExpiryDatePicker, 1, 10);
        grid.add(new Label("AMC Cost (₹):"), 0, 11); grid.add(amcCostField, 1, 11);
        grid.add(new Label("AMC Notes:"), 0, 12); grid.add(amcDetailsArea, 1, 12);

        dialog.getDialogPane().setContent(new ScrollPane(grid));

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveBtnType) {
                asset.setName(nameField.getText().trim());
                asset.setCategory(categoryCombo.getValue());
                asset.setSerialNumber(serialField.getText().trim());
                asset.setPurchaseDate(purchaseDatePicker.getValue() != null ? purchaseDatePicker.getValue().toString() : null);
                try { asset.setPurchaseCost(Double.parseDouble(costField.getText().trim())); } catch (Exception ignored) {}
                asset.setWarrantyExpiryDate(warrantyDatePicker.getValue() != null ? warrantyDatePicker.getValue().toString() : null);
                asset.setStatus(statusCombo.getValue());

                asset.setAmcVendor(amcVendorCombo.getValue());
                asset.setAmcStartDate(amcStartDatePicker.getValue() != null ? amcStartDatePicker.getValue().toString() : null);
                asset.setAmcExpiryDate(amcExpiryDatePicker.getValue() != null ? amcExpiryDatePicker.getValue().toString() : null);
                try { asset.setAmcCost(Double.parseDouble(amcCostField.getText().trim())); } catch (Exception ignored) {}
                asset.setAmcDetails(amcDetailsArea.getText().trim());

                return asset;
            }
            return null;
        });

        Optional<AssetEntity> result = dialog.showAndWait();
        result.ifPresent(a -> {
            if (!a.getName().isEmpty()) {
                operationsService.saveAsset(a);
                loadAssets();
                showAssetDetails(a);
            }
        });
    }

    @FXML
    private void handleAddVendor() {
        showVendorDialog(new VendorEntity());
    }

    @FXML
    private void handleEditVendor() {
        VendorEntity selected = vendorTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Selection Required");
            alert.setHeaderText(null);
            alert.setContentText("Please select a vendor from the table to edit.");
            alert.showAndWait();
            return;
        }
        showVendorDialog(selected);
    }

    private void showVendorDialog(VendorEntity vendor) {
        boolean isEdit = vendor.getId() != null;
        Dialog<VendorEntity> dialog = new Dialog<>();
        dialog.setTitle(isEdit ? "Edit Vendor Profile" : "Add Vendor");
        dialog.setHeaderText(isEdit ? "Update Vendor Profile & Contact Details:" : "Enter Vendor Directory Details:");

        ButtonType saveBtnType = new ButtonType(isEdit ? "Update" : "Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtnType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(15));

        TextField nameField = new TextField(vendor.getName() != null ? vendor.getName() : "");
        nameField.setPromptText("Vendor Company Name");

        TextField catField = new TextField(vendor.getCategory() != null ? vendor.getCategory() : "");
        catField.setPromptText("Category (e.g. SECURITY, ELEVATOR, MAINTENANCE)");

        TextField phoneField = new TextField(vendor.getPhone() != null ? vendor.getPhone() : "");
        phoneField.setPromptText("Primary Contact Phone Number");

        TextField contactPersonField = new TextField(vendor.getContactPerson() != null ? vendor.getContactPerson() : "");
        contactPersonField.setPromptText("Contact Person Name");

        TextField emailField = new TextField(vendor.getEmail() != null ? vendor.getEmail() : "");
        emailField.setPromptText("Email Address");

        TextArea addressArea = new TextArea(vendor.getAddress() != null ? vendor.getAddress() : "");
        addressArea.setPromptText("Office / Business Address...");
        addressArea.setPrefRowCount(3);

        ComboBox<String> statusCombo = new ComboBox<>(FXCollections.observableArrayList("ACTIVE", "INACTIVE"));
        statusCombo.getSelectionModel().select(vendor.getId() == null || vendor.isActive() ? "ACTIVE" : "INACTIVE");

        grid.add(new Label("Company Name:"), 0, 0); grid.add(nameField, 1, 0);
        grid.add(new Label("Category:"), 0, 1); grid.add(catField, 1, 1);
        grid.add(new Label("Phone:"), 0, 2); grid.add(phoneField, 1, 2);
        grid.add(new Label("Contact Person:"), 0, 3); grid.add(contactPersonField, 1, 3);
        grid.add(new Label("Email:"), 0, 4); grid.add(emailField, 1, 4);
        grid.add(new Label("Office Address:"), 0, 5); grid.add(addressArea, 1, 5);
        grid.add(new Label("Status:"), 0, 6); grid.add(statusCombo, 1, 6);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveBtnType) {
                vendor.setName(nameField.getText().trim());
                vendor.setCategory(catField.getText().trim());
                vendor.setPhone(phoneField.getText().trim());
                vendor.setContactPerson(contactPersonField.getText().trim());
                vendor.setEmail(emailField.getText().trim());
                vendor.setAddress(addressArea.getText().trim());
                vendor.setActive("ACTIVE".equals(statusCombo.getValue()));
                return vendor;
            }
            return null;
        });

        Optional<VendorEntity> result = dialog.showAndWait();
        result.ifPresent(v -> {
            if (!v.getName().isEmpty()) {
                operationsService.saveVendor(v);
                loadVendors();
                showVendorDetails(v);
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

    @FXML
    private void handleDeactivateStaff() {
        SocietyStaffEntity selected = staffTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Selection Required");
            alert.setHeaderText(null);
            alert.setContentText("Please select a staff member from the table to deactivate.");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Deactivate / Terminate Staff");
        alert.setHeaderText("End Term / Deactivate " + selected.getName());
        alert.setContentText("Are you sure you want to set this staff member's status to INACTIVE / Terminated?");

        alert.showAndWait()
                .filter(btn -> btn == ButtonType.OK)
                .ifPresent(btn -> {
                    selected.setActive(false);
                    operationsService.saveStaff(selected);
                    loadStaff();
                });
    }
}
