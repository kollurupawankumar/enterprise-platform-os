package com.society.member.controller;

import com.society.common.controller.BaseController;
import com.society.common.navigation.NavigationManager;
import com.society.common.navigation.View;
import com.society.common.service.FileStorageService;
import com.society.member.context.MemberContext;
import com.society.member.dto.JointOwnerDto;
import com.society.member.dto.MemberDto;
import com.society.member.dto.NomineeDto;
import com.society.member.entity.MemberStatus;
import com.society.member.service.MemberService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class MemberRegistrationController extends BaseController {

    private final MemberService memberService;
    private final MemberContext memberContext;
    private final FileStorageService fileStorageService;

    @FXML private Label titleLabel;

    // Tab 1: Demographics
    @FXML private TextField memberNumberField;
    @FXML private TextField membershipNumberField;
    @FXML private TextField firstNameField;
    @FXML private TextField middleNameField;
    @FXML private TextField lastNameField;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private DatePicker dobDatePicker;
    @FXML private TextField occupationField;
    @FXML private TextField mobileNumberField;
    @FXML private TextField emailField;
    @FXML private TextField emergencyContactNameField;
    @FXML private TextField emergencyContactPhoneField;
    @FXML private ComboBox<String> statusComboBox;

    // Tab 2: Classification
    @FXML private ComboBox<String> memberTypeComboBox;
    @FXML private DatePicker admissionDatePicker;
    @FXML private TextField resolutionNumberField;
    @FXML private DatePicker resolutionDatePicker;

    // Tab 3: Address
    @FXML private TextArea permanentAddressArea;
    @FXML private TextArea correspondenceAddressArea;

    // Tab 4: Joint Owners & Nominees
    @FXML private TableView<JointOwnerDto> jointOwnerTable;
    @FXML private TableColumn<JointOwnerDto, String> joFirstNameCol;
    @FXML private TableColumn<JointOwnerDto, String> joLastNameCol;
    @FXML private TableColumn<JointOwnerDto, String> joRelationCol;
    @FXML private TableColumn<JointOwnerDto, String> joAadhaarCol;
    @FXML private TableColumn<JointOwnerDto, String> joPanCol;

    @FXML private TableView<NomineeDto> nomineeTable;
    @FXML private TableColumn<NomineeDto, String> nomFirstNameCol;
    @FXML private TableColumn<NomineeDto, String> nomLastNameCol;
    @FXML private TableColumn<NomineeDto, String> nomRelationCol;
    @FXML private TableColumn<NomineeDto, Double> nomShareCol;

    private final ObservableList<JointOwnerDto> jointOwnerList = FXCollections.observableArrayList();
    private final ObservableList<NomineeDto> nomineeList = FXCollections.observableArrayList();

    // Tab 5: Documents
    @FXML private TextField aadhaarNumberField;
    @FXML private TextField panNumberField;
    @FXML private TextField photoPathField;
    @FXML private TextField aadhaarDocPathField;
    @FXML private TextField panDocPathField;

    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public MemberRegistrationController(
            NavigationManager navigationManager,
            MemberService memberService,
            MemberContext memberContext,
            FileStorageService fileStorageService) {

        super(navigationManager);
        this.memberService = memberService;
        this.memberContext = memberContext;
        this.fileStorageService = fileStorageService;
    }

    @FXML
    public void initialize() {
        memberNumberField.setEditable(false);

        statusComboBox.getItems().setAll(
                MemberStatus.ACTIVE.name(),
                MemberStatus.INACTIVE.name(),
                MemberStatus.TRANSFERRED.name(),
                MemberStatus.RESIGNED.name(),
                MemberStatus.DECEASED.name()
        );

        genderComboBox.getItems().setAll("MALE", "FEMALE", "OTHER");
        memberTypeComboBox.getItems().setAll("ORIGINAL", "ASSOCIATE", "NOMINAL", "CORPORATE");

        setupJointOwnerTable();
        setupNomineeTable();

        if (memberContext.isEditMode()) {
            MemberDto selected = memberContext.getSelectedMember();
            if (titleLabel != null) {
                titleLabel.setText("Edit Member Details");
            }
            populateFields(selected);
        } else {
            if (titleLabel != null) {
                titleLabel.setText("Register Member");
            }
            statusComboBox.getSelectionModel().select(MemberStatus.ACTIVE.name());
            memberTypeComboBox.getSelectionModel().select("ORIGINAL");
            loadGeneratedMemberNumber();
        }
    }

    private void setupJointOwnerTable() {
        joFirstNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().firstName()));
        joLastNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().lastName()));
        joRelationCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().relationship()));
        joAadhaarCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().aadhaarNumber()));
        joPanCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().panNumber()));
        jointOwnerTable.setItems(jointOwnerList);
    }

    private void setupNomineeTable() {
        nomFirstNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().firstName()));
        nomLastNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().lastName()));
        nomRelationCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().relationship()));
        nomShareCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().sharePercentage()).asObject());
        nomineeTable.setItems(nomineeList);
    }

    private void populateFields(MemberDto dto) {
        memberNumberField.setText(dto.memberNumber());
        membershipNumberField.setText(dto.membershipNumber());
        firstNameField.setText(dto.firstName());
        middleNameField.setText(dto.middleName());
        lastNameField.setText(dto.lastName());
        if (dto.gender() != null) genderComboBox.getSelectionModel().select(dto.gender());
        if (dto.dob() != null && !dto.dob().isBlank()) {
            try { dobDatePicker.setValue(LocalDate.parse(dto.dob(), DATE_FORMATTER)); } catch (Exception ignored) {}
        }
        occupationField.setText(dto.occupation());
        mobileNumberField.setText(dto.mobileNumber());
        emailField.setText(dto.email());
        emergencyContactNameField.setText(dto.emergencyContactName());
        emergencyContactPhoneField.setText(dto.emergencyContactPhone());

        if (dto.status() != null) statusComboBox.getSelectionModel().select(dto.status().name());

        if (dto.memberType() != null) memberTypeComboBox.getSelectionModel().select(dto.memberType());
        if (dto.admissionDate() != null && !dto.admissionDate().isBlank()) {
            try { admissionDatePicker.setValue(LocalDate.parse(dto.admissionDate(), DATE_FORMATTER)); } catch (Exception ignored) {}
        }
        resolutionNumberField.setText(dto.resolutionNumber());
        if (dto.resolutionDate() != null && !dto.resolutionDate().isBlank()) {
            try { resolutionDatePicker.setValue(LocalDate.parse(dto.resolutionDate(), DATE_FORMATTER)); } catch (Exception ignored) {}
        }

        permanentAddressArea.setText(dto.permanentAddress());
        correspondenceAddressArea.setText(dto.correspondenceAddress());

        aadhaarNumberField.setText(dto.aadhaarNumber());
        panNumberField.setText(dto.panNumber());

        photoPathField.setText(dto.photoPath());
        aadhaarDocPathField.setText(dto.aadhaarDocPath());
        panDocPathField.setText(dto.panDocPath());

        if (dto.jointOwners() != null) {
            jointOwnerList.setAll(dto.jointOwners());
        }
        if (dto.nominees() != null) {
            nomineeList.setAll(dto.nominees());
        }
    }

    private void loadGeneratedMemberNumber() {
        memberNumberField.setText(memberService.generateMemberNumber());
    }

    @FXML
    private void addJointOwner() {
        Dialog<JointOwnerDto> dialog = new Dialog<>();
        dialog.setTitle("Add Joint Owner");
        dialog.setHeaderText("Enter Joint Owner Details");

        ButtonType saveButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField fn = new TextField(); fn.setPromptText("First Name");
        TextField ln = new TextField(); ln.setPromptText("Last Name");
        TextField rel = new TextField(); rel.setPromptText("Relationship");
        TextField aadh = new TextField(); aadh.setPromptText("Aadhaar Number");
        TextField pan = new TextField(); pan.setPromptText("PAN Number");

        grid.add(new Label("First Name:"), 0, 0); grid.add(fn, 1, 0);
        grid.add(new Label("Last Name:"), 0, 1); grid.add(ln, 1, 1);
        grid.add(new Label("Relationship:"), 0, 2); grid.add(rel, 1, 2);
        grid.add(new Label("Aadhaar:"), 0, 3); grid.add(aadh, 1, 3);
        grid.add(new Label("PAN:"), 0, 4); grid.add(pan, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new JointOwnerDto(null, fn.getText(), ln.getText(), rel.getText(), aadh.getText(), pan.getText());
            }
            return null;
        });

        Optional<JointOwnerDto> result = dialog.showAndWait();
        result.ifPresent(jointOwnerList::add);
    }

    @FXML
    private void removeJointOwner() {
        JointOwnerDto selected = jointOwnerTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            jointOwnerList.remove(selected);
        }
    }

    @FXML
    private void addNominee() {
        Dialog<NomineeDto> dialog = new Dialog<>();
        dialog.setTitle("Add Nominee");
        dialog.setHeaderText("Enter Nominee Details");

        ButtonType saveButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField fn = new TextField(); fn.setPromptText("First Name");
        TextField ln = new TextField(); ln.setPromptText("Last Name");
        TextField rel = new TextField(); rel.setPromptText("Relationship");
        TextField share = new TextField(); share.setPromptText("Share % (e.g. 100)");

        grid.add(new Label("First Name:"), 0, 0); grid.add(fn, 1, 0);
        grid.add(new Label("Last Name:"), 0, 1); grid.add(ln, 1, 1);
        grid.add(new Label("Relationship:"), 0, 2); grid.add(rel, 1, 2);
        grid.add(new Label("Share %:"), 0, 3); grid.add(share, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                double pct = 0.0;
                try { pct = Double.parseDouble(share.getText()); } catch (Exception ignored) {}
                return new NomineeDto(null, fn.getText(), ln.getText(), rel.getText(), pct);
            }
            return null;
        });

        Optional<NomineeDto> result = dialog.showAndWait();
        result.ifPresent(nomineeList::add);
    }

    @FXML
    private void removeNominee() {
        NomineeDto selected = nomineeTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            nomineeList.remove(selected);
        }
    }

    @FXML
    private void browsePhoto() {
        File file = chooseFile("Select Member Photo", new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png"));
        if (file != null) {
            try {
                String first = firstNameField.getText() != null && !firstNameField.getText().isBlank() ? firstNameField.getText().trim() : "member";
                String last = lastNameField.getText() != null && !lastNameField.getText().isBlank() ? lastNameField.getText().trim() : "";
                String unit = memberNumberField.getText() != null && !memberNumberField.getText().isBlank() ? memberNumberField.getText().trim() : "unit";

                String ext = ".jpg";
                int idx = file.getName().lastIndexOf('.');
                if (idx > 0) {
                    ext = file.getName().substring(idx);
                }

                String cleanFirst = first.replaceAll("[^a-zA-Z0-9]", "_").toLowerCase();
                String cleanLast = last.replaceAll("[^a-zA-Z0-9]", "_").toLowerCase();
                String cleanUnit = unit.replaceAll("[^a-zA-Z0-9]", "_").toLowerCase();

                String customFileName = cleanLast.isEmpty()
                        ? cleanFirst + "_" + cleanUnit + ext
                        : cleanFirst + "_" + cleanLast + "_" + cleanUnit + ext;

                String relPath = fileStorageService.storeFileWithName(file, "photos", customFileName);
                photoPathField.setText(relPath);
            } catch (Exception ex) {
                showError("Failed to copy photo file: " + ex.getMessage());
            }
        }
    }

    @FXML
    private void browseAadhaarDoc() {
        File file = chooseFile("Select Aadhaar Document", new FileChooser.ExtensionFilter("PDF or Images", "*.pdf", "*.jpg", "*.jpeg", "*.png"));
        if (file != null) {
            try {
                String relPath = fileStorageService.storeFile(file, "kyc");
                aadhaarDocPathField.setText(relPath);
            } catch (Exception ex) {
                showError("Failed to copy document file: " + ex.getMessage());
            }
        }
    }

    @FXML
    private void browsePanDoc() {
        File file = chooseFile("Select PAN Document", new FileChooser.ExtensionFilter("PDF or Images", "*.pdf", "*.jpg", "*.jpeg", "*.png"));
        if (file != null) {
            try {
                String relPath = fileStorageService.storeFile(file, "kyc");
                panDocPathField.setText(relPath);
            } catch (Exception ex) {
                showError("Failed to copy document file: " + ex.getMessage());
            }
        }
    }

    private File chooseFile(String title, FileChooser.ExtensionFilter filter) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().add(filter);
        return fileChooser.showOpenDialog(saveButton.getScene().getWindow());
    }

    @FXML
    private void save() {
        try {
            boolean isEdit = memberContext.isEditMode();
            Integer id = isEdit ? memberContext.getSelectedMember().id() : null;

            MemberDto dto = buildMember(id);

            if (isEdit) {
                memberService.update(dto);
                showSuccess("Member updated successfully.");
            } else {
                memberService.register(dto);
                showSuccess("Member registered successfully.");
            }

            memberContext.clearSelectedMember();
            navigationManager.navigate(View.MEMBERS);

        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        } catch (Exception ex) {
            String message = ex.getMessage();
            if (ex.getCause() != null && ex.getCause().getMessage() != null) {
                message = ex.getCause().getMessage();
            }
            showError("Unable to save member details:\n" + (message != null ? message : ex.toString()));
            ex.printStackTrace();
        }
    }

    @FXML
    private void cancel() {
        memberContext.clearSelectedMember();
        navigationManager.navigate(View.MEMBERS);
    }

    private MemberDto buildMember(Integer id) {
        String statusVal = statusComboBox.getValue();
        MemberStatus status = (statusVal != null && !statusVal.isBlank())
                ? MemberStatus.valueOf(statusVal)
                : MemberStatus.ACTIVE;

        String dobStr = dobDatePicker.getValue() != null ? dobDatePicker.getValue().format(DATE_FORMATTER) : null;
        String admStr = admissionDatePicker.getValue() != null ? admissionDatePicker.getValue().format(DATE_FORMATTER) : null;
        String resStr = resolutionDatePicker.getValue() != null ? resolutionDatePicker.getValue().format(DATE_FORMATTER) : null;

        return new MemberDto(
                id,
                memberNumberField.getText(),
                trimOrNull(membershipNumberField.getText()),
                trimOrNull(firstNameField.getText()),
                trimOrNull(middleNameField.getText()),
                trimOrNull(lastNameField.getText()),
                trimOrNull(mobileNumberField.getText()),
                trimOrNull(emailField.getText()),
                genderComboBox.getValue(),
                dobStr,
                trimOrNull(occupationField.getText()),
                trimOrNull(emergencyContactNameField.getText()),
                trimOrNull(emergencyContactPhoneField.getText()),
                memberTypeComboBox.getValue(),
                admStr,
                trimOrNull(resolutionNumberField.getText()),
                resStr,
                trimOrNull(permanentAddressArea.getText()),
                trimOrNull(correspondenceAddressArea.getText()),
                trimOrNull(photoPathField.getText()),
                trimOrNull(aadhaarDocPathField.getText()),
                trimOrNull(panDocPathField.getText()),
                trimOrNull(aadhaarNumberField.getText()),
                trimOrNull(panNumberField.getText()),
                status,
                true,
                new ArrayList<>(jointOwnerList),
                new ArrayList<>(nomineeList)
        );
    }

    private String trimOrNull(String val) {
        if (val == null || val.isBlank()) return null;
        return val.trim();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}