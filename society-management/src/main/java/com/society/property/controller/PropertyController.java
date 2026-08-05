package com.society.property.controller;

import com.society.common.controller.BaseController;
import com.society.common.navigation.NavigationManager;
import com.society.member.entity.MemberEntity;
import com.society.member.repository.MemberRepository;
import com.society.member.repository.JointOwnerRepository;
import com.society.member.repository.NomineeRepository;
import com.society.member.entity.JointOwnerEntity;
import com.society.member.entity.NomineeEntity;
import com.society.property.entity.PropertyEntity;
import com.society.property.entity.OwnershipHistoryEntity;
import com.society.property.service.PropertyService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PropertyController extends BaseController {

    private final PropertyService propertyService;
    private final MemberRepository memberRepository;
    private final JointOwnerRepository jointOwnerRepository;
    private final NomineeRepository nomineeRepository;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<PropertyEntity> propertyTable;

    @FXML
    private TableColumn<PropertyEntity, String> propertyNumCol;

    @FXML
    private TableColumn<PropertyEntity, String> blockCol;

    @FXML
    private TableColumn<PropertyEntity, String> typeCol;

    @FXML
    private TableColumn<PropertyEntity, String> ownerCol;

    @FXML
    private Label detailUnitLabel;

    @FXML
    private Label detailBlockLabel;

    @FXML
    private Label detailTypeLabel;

    @FXML
    private Label detailOwnerLabel;

    @FXML
    private ListView<String> associationList;

    private final ObservableList<PropertyEntity> properties = FXCollections.observableArrayList();

    public PropertyController(
            NavigationManager navigationManager,
            PropertyService propertyService,
            MemberRepository memberRepository,
            JointOwnerRepository jointOwnerRepository,
            NomineeRepository nomineeRepository) {
        super(navigationManager);
        this.propertyService = propertyService;
        this.memberRepository = memberRepository;
        this.jointOwnerRepository = jointOwnerRepository;
        this.nomineeRepository = nomineeRepository;
    }

    @FXML
    public void initialize() {
        propertyNumCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPropertyNumber()));
        blockCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getBlock()));
        typeCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getType()));
        ownerCol.setCellValueFactory(cell -> {
            MemberEntity owner = cell.getValue().getCurrentOwner();
            return new SimpleStringProperty(owner != null ? owner.getFirstName() + " " + owner.getLastName() : "No Active Owner");
        });

        loadProperties();

        propertyTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            showPropertyDetails(newVal);
        });

        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterProperties(newVal));
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
            return;
        }

        detailUnitLabel.setText(property.getPropertyNumber());
        detailBlockLabel.setText(property.getBlock());
        detailTypeLabel.setText(property.getType());

        MemberEntity owner = property.getCurrentOwner();
        if (owner != null) {
            detailOwnerLabel.setText(owner.getFirstName() + " " + owner.getLastName() + " (" + owner.getMemberNumber() + ")");
            loadAssociations(owner.getId());
        } else {
            detailOwnerLabel.setText("No Active Owner");
            associationList.getItems().clear();
        }
    }

    private void loadAssociations(Integer memberId) {
        associationList.getItems().clear();
        List<JointOwnerEntity> jointOwners = jointOwnerRepository.findByMemberId(memberId);
        for (JointOwnerEntity jo : jointOwners) {
            associationList.getItems().add("Joint Owner: " + jo.getFirstName() + " " + jo.getLastName() + " (" + jo.getRelationship() + ")");
        }

        List<NomineeEntity> nominees = nomineeRepository.findByMemberId(memberId);
        for (NomineeEntity nominee : nominees) {
            associationList.getItems().add("Nominee: " + nominee.getFirstName() + " " + nominee.getLastName() + " (" + nominee.getSharePercentage() + "%)");
        }
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
                propertyService.saveProperty(p);
                loadProperties();
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
            showPropertyDetails(selected);
        });
    }
}
