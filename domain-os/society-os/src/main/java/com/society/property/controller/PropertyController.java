package com.society.property.controller;

import com.society.common.controller.BaseController;
import com.society.common.navigation.NavigationManager;
import com.society.member.entity.JointOwnerEntity;
import com.society.member.entity.MemberEntity;
import com.society.member.entity.NomineeEntity;
import com.society.member.repository.JointOwnerRepository;
import com.society.member.repository.MemberRepository;
import com.society.member.repository.NomineeRepository;
import com.society.property.dto.OwnershipHistoryDto;
import com.society.property.entity.OwnershipHistoryEntity;
import com.society.property.entity.PropertyEntity;
import com.society.property.service.PropertyService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class PropertyController extends BaseController {

    private final PropertyService propertyService;
    private final MemberRepository memberRepository;
    private final JointOwnerRepository jointOwnerRepository;
    private final NomineeRepository nomineeRepository;

    @FXML private TextField searchField;
    @FXML private TableView<PropertyEntity> propertyTable;
    @FXML private TableColumn<PropertyEntity, String> propertyNumCol;
    @FXML private TableColumn<PropertyEntity, String> blockCol;
    @FXML private TableColumn<PropertyEntity, String> typeCol;
    @FXML private TableColumn<PropertyEntity, String> ownerCol;

    @FXML private Label detailUnitLabel;
    @FXML private Label detailBlockLabel;
    @FXML private Label detailTypeLabel;
    @FXML private Label detailOwnerLabel;

    @FXML private ListView<String> associationList;

    // Ownership History Table
    @FXML private TableView<OwnershipHistoryDto> historyTable;
    @FXML private TableColumn<OwnershipHistoryDto, String> histOwnerCol;
    @FXML private TableColumn<OwnershipHistoryDto, String> histFromCol;
    @FXML private TableColumn<OwnershipHistoryDto, String> histToCol;
    @FXML private TableColumn<OwnershipHistoryDto, String> histStatusCol;

    private final ObservableList<PropertyEntity> properties = FXCollections.observableArrayList();
    private final ObservableList<OwnershipHistoryDto> historyList = FXCollections.observableArrayList();

    @FXML private Button addUnitBtn;
    @FXML private Button bulkImportBtn;
    @FXML private Button transferBtn;

    private final com.society.user.context.UserContext userContext;

    public PropertyController(
            NavigationManager navigationManager,
            PropertyService propertyService,
            MemberRepository memberRepository,
            JointOwnerRepository jointOwnerRepository,
            NomineeRepository nomineeRepository,
            com.society.user.context.UserContext userContext) {
        super(navigationManager);
        this.propertyService = propertyService;
        this.memberRepository = memberRepository;
        this.jointOwnerRepository = jointOwnerRepository;
        this.nomineeRepository = nomineeRepository;
        this.userContext = userContext;
    }

    @FXML
    public void initialize() {
        propertyNumCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPropertyNumber()));
        blockCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getBlock()));
        typeCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getType()));
        ownerCol.setCellValueFactory(cell -> {
            MemberEntity m = cell.getValue().getCurrentOwner();
            return new SimpleStringProperty(m != null ? m.getFirstName() + " " + (m.getLastName() != null ? m.getLastName() : "") : "Unassigned");
        });

        // History Table Columns
        histOwnerCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().ownerName()));
        histFromCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().fromDate()));
        histToCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().toDate() != null ? c.getValue().toDate() : "Present"));
        histStatusCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().status()));
        historyTable.setItems(historyList);

        propertyTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            showPropertyDetails(newVal);
        });

        loadProperties();
        applyRolePermissions();
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterProperties(newVal));
    }

    private void applyRolePermissions() {
        boolean canEdit = userContext.canEdit("PROPERTIES");
        if (addUnitBtn != null) { addUnitBtn.setVisible(canEdit); addUnitBtn.setManaged(canEdit); }
        if (bulkImportBtn != null) { bulkImportBtn.setVisible(canEdit); bulkImportBtn.setManaged(canEdit); }
        if (transferBtn != null) { transferBtn.setVisible(canEdit); transferBtn.setManaged(canEdit); }
    }

    private void loadProperties() {
        properties.setAll(propertyService.getAllProperties());
        propertyTable.setItems(properties);
    }

    private void filterProperties(String query) {
        if (query == null || query.isEmpty()) {
            propertyTable.setItems(properties);
            return;
        }
        String lower = query.toLowerCase();
        ObservableList<PropertyEntity> filtered = properties.filtered(p ->
            p.getPropertyNumber().toLowerCase().contains(lower) || p.getBlock().toLowerCase().contains(lower)
        );
        propertyTable.setItems(filtered);
    }

    private void showPropertyDetails(PropertyEntity property) {
        if (property == null) {
            detailUnitLabel.setText("-");
            detailBlockLabel.setText("-");
            detailTypeLabel.setText("-");
            detailOwnerLabel.setText("-");
            associationList.getItems().clear();
            historyList.clear();
            return;
        }

        detailUnitLabel.setText(property.getPropertyNumber());
        detailBlockLabel.setText(property.getBlock());
        detailTypeLabel.setText(property.getType());

        MemberEntity owner = property.getCurrentOwner();
        if (owner != null) {
            detailOwnerLabel.setText(owner.getFirstName() + " " + (owner.getLastName() != null ? owner.getLastName() : "") + " (" + owner.getMemberNumber() + ")");
            loadAssociations(owner.getId());
        } else {
            detailOwnerLabel.setText("No Active Owner");
            associationList.getItems().clear();
        }

        loadOwnershipHistory(property.getId());
    }

    private void loadAssociations(Integer memberId) {
        associationList.getItems().clear();
        List<JointOwnerEntity> jointOwners = jointOwnerRepository.findByMemberId(memberId);
        for (JointOwnerEntity jo : jointOwners) {
            associationList.getItems().add("Joint Owner: " + jo.getFirstName() + " " + (jo.getLastName() != null ? jo.getLastName() : "") + " (" + jo.getRelationship() + ")");
        }

        List<NomineeEntity> nominees = nomineeRepository.findByMemberId(memberId);
        for (NomineeEntity nominee : nominees) {
            associationList.getItems().add("Nominee: " + nominee.getFirstName() + " " + (nominee.getLastName() != null ? nominee.getLastName() : "") + " (" + nominee.getSharePercentage() + "%)");
        }
    }

    private void loadOwnershipHistory(Integer propertyId) {
        historyList.clear();
        List<OwnershipHistoryEntity> histories = propertyService.getOwnershipHistory(propertyId);
        List<OwnershipHistoryDto> dtos = new ArrayList<>();
        for (OwnershipHistoryEntity h : histories) {
            MemberEntity m = h.getMember();
            String name = m != null ? m.getFirstName() + " " + (m.getLastName() != null ? m.getLastName() : "") + " (" + m.getMemberNumber() + ")" : "Unknown Member";
            String from = h.getFromDate() != null ? h.getFromDate() : "-";
            String to = h.getToDate() != null ? h.getToDate() : "Present";
            String status = h.getToDate() == null ? "CURRENT OWNER" : "PREVIOUS OWNER";
            dtos.add(new OwnershipHistoryDto(h.getId(), name, from, to, status));
        }
        historyList.setAll(dtos);
    }

    @FXML
    private void handleAddProperty() {
        Dialog<PropertyEntity> dialog = new Dialog<>();
        dialog.setTitle("Add New Property Unit");
        dialog.setHeaderText("Specify Unit Registration details:");

        ButtonType saveButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField unitField = new TextField();
        unitField.setPromptText("e.g. 301");
        TextField blockField = new TextField();
        blockField.setPromptText("e.g. A Wing");
        ComboBox<String> typeCombo = new ComboBox<>(FXCollections.observableArrayList("FLAT", "VILLA", "SHOP", "GARAGE"));
        typeCombo.getSelectionModel().selectFirst();

        grid.add(new Label("Unit Number:"), 0, 0);
        grid.add(unitField, 1, 0);
        grid.add(new Label("Block:"), 0, 1);
        grid.add(blockField, 1, 1);
        grid.add(new Label("Type:"), 0, 2);
        grid.add(typeCombo, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                PropertyEntity p = new PropertyEntity();
                p.setPropertyNumber(unitField.getText().trim());
                p.setBlock(blockField.getText().trim());
                p.setType(typeCombo.getValue());
                return p;
            }
            return null;
        });

        Optional<PropertyEntity> result = dialog.showAndWait();
        result.ifPresent(p -> {
            if (!p.getPropertyNumber().isEmpty() && !p.getBlock().isEmpty()) {
                try {
                    propertyService.saveProperty(p);
                    loadProperties();
                } catch (IllegalArgumentException ex) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Duplicate Property");
                    alert.setHeaderText("Unit Already Exists");
                    alert.setContentText(ex.getMessage());
                    alert.showAndWait();
                }
            }
        });
    }

    @FXML
    private void handleTransfer() {
        PropertyEntity selected = propertyTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Selection Required");
            alert.setHeaderText(null);
            alert.setContentText("Please select a property unit to transfer.");
            alert.showAndWait();
            return;
        }

        List<MemberEntity> members = memberRepository.findAll();
        if (members.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Members Missing");
            alert.setHeaderText(null);
            alert.setContentText("No registered members found. Please add members first.");
            alert.showAndWait();
            return;
        }

        ChoiceDialog<MemberEntity> dialog = new ChoiceDialog<>(members.get(0), members);
        dialog.setTitle("Transfer Ownership");
        dialog.setHeaderText("Transfer Unit " + selected.getPropertyNumber());
        dialog.setContentText("Select new Owner:");

        Optional<MemberEntity> result = dialog.showAndWait();
        result.ifPresent(newOwner -> {
            propertyService.transferOwnership(selected.getId(), newOwner.getId(), java.time.LocalDate.now().toString());
            loadProperties();
            PropertyEntity updated = propertyService.getPropertyById(selected.getId()).orElse(null);
            showPropertyDetails(updated);
        });
    }

    @FXML
    private void downloadTemplate() {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Save Property CSV Template");
        fileChooser.setInitialFileName("property_import_template.csv");
        fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        java.io.File file = fileChooser.showSaveDialog(propertyTable.getScene().getWindow());

        if (file != null) {
            String template = "Property Number,Block,Type\n" +
                    "101,A Wing,FLAT\n" +
                    "102,A Wing,FLAT\n" +
                    "V-01,Phase 1,VILLA\n" +
                    "S-01,Commercial Block,SHOP\n";
            try {
                java.nio.file.Files.writeString(file.toPath(), template);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("Property CSV Template downloaded successfully!");
                alert.showAndWait();
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Failed to save template: " + ex.getMessage());
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void bulkImportCsv() {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Select Property CSV File to Import");
        fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        java.io.File file = fileChooser.showOpenDialog(propertyTable.getScene().getWindow());

        if (file != null) {
            try {
                List<String> lines = java.nio.file.Files.readAllLines(file.toPath());
                if (lines.size() <= 1) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("The selected CSV file is empty or has no data rows.");
                    alert.showAndWait();
                    return;
                }

                int successCount = 0;
                int failCount = 0;

                for (int i = 1; i < lines.size(); i++) {
                    String line = lines.get(i).trim();
                    if (line.isBlank()) continue;

                    String[] cols = line.split(",", -1);
                    if (cols.length >= 3) {
                        try {
                            String pNo = cols[0].trim();
                            String block = cols[1].trim();
                            String type = cols[2].trim().toUpperCase();

                            PropertyEntity p = new PropertyEntity();
                            p.setPropertyNumber(pNo);
                            p.setBlock(block);
                            p.setType(type);

                            propertyService.saveProperty(p);
                            successCount++;
                        } catch (Exception ex) {
                            failCount++;
                        }
                    }
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Bulk Import Result");
                alert.setHeaderText(null);
                alert.setContentText("Property Bulk Import Complete!\nSuccessfully Imported: " + successCount + " units.\nFailed/Skipped (Duplicates): " + failCount);
                alert.showAndWait();
                loadProperties();
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Failed to process CSV file: " + ex.getMessage());
                alert.showAndWait();
            }
        }
    }
}
