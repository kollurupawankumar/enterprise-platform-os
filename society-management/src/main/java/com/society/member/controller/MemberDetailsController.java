package com.society.member.controller;

import com.society.common.controller.BaseController;
import com.society.common.navigation.NavigationManager;
import com.society.common.navigation.View;
import com.society.member.context.MemberContext;
import com.society.member.dto.JointOwnerDto;
import com.society.member.dto.MemberDto;
import com.society.member.dto.NomineeDto;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.springframework.stereotype.Component;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Component
public class MemberDetailsController extends BaseController {

    private final MemberContext memberContext;

    @FXML private ImageView photoImageView;
    @FXML private Label nameLabel;
    @FXML private Label statusBadge;
    @FXML private Label memberNumLabel;
    @FXML private Label membershipNumLabel;

    // Tab 1: Demographics
    @FXML private Label firstNameVal;
    @FXML private Label middleNameVal;
    @FXML private Label lastNameVal;
    @FXML private Label genderVal;
    @FXML private Label dobVal;
    @FXML private Label occupationVal;
    @FXML private Label mobileVal;
    @FXML private Label emailVal;
    @FXML private Label emergencyNameVal;
    @FXML private Label emergencyPhoneVal;

    // Tab 2: Classification
    @FXML private Label memberTypeVal;
    @FXML private Label admissionDateVal;
    @FXML private Label resolutionNumVal;
    @FXML private Label resolutionDateVal;

    // Tab 3: Address
    @FXML private Label permanentAddressVal;
    @FXML private Label correspondenceAddressVal;

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
    @FXML private Label aadhaarNumVal;
    @FXML private Label panNumVal;
    @FXML private Label aadhaarPathLabel;
    @FXML private Button viewAadhaarBtn;
    @FXML private Button downloadAadhaarBtn;
    @FXML private Label panPathLabel;
    @FXML private Button viewPanBtn;
    @FXML private Button downloadPanBtn;

    private MemberDto currentMember;

    public MemberDetailsController(NavigationManager navigationManager, MemberContext memberContext) {
        super(navigationManager);
        this.memberContext = memberContext;
    }

    @FXML
    public void initialize() {
        setupTables();

        currentMember = memberContext.getSelectedMember();
        if (currentMember != null) {
            displayMemberDetails(currentMember);
        }
    }

    private void setupTables() {
        joFirstNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().firstName()));
        joLastNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().lastName()));
        joRelationCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().relationship()));
        joAadhaarCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().aadhaarNumber()));
        joPanCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().panNumber()));
        jointOwnerTable.setItems(jointOwnerList);

        nomFirstNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().firstName()));
        nomLastNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().lastName()));
        nomRelationCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().relationship()));
        nomShareCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().sharePercentage()).asObject());
        nomineeTable.setItems(nomineeList);
    }

    private void displayMemberDetails(MemberDto m) {
        // Header
        String fullName = ((m.firstName() != null ? m.firstName() : "") + " " +
                (m.middleName() != null ? m.middleName() + " " : "") +
                (m.lastName() != null ? m.lastName() : "")).trim();
        nameLabel.setText(fullName.isEmpty() ? "Member Profile" : fullName);
        statusBadge.setText(m.status() != null ? m.status().name() : "ACTIVE");
        memberNumLabel.setText("Member No: " + (m.memberNumber() != null ? m.memberNumber() : "-"));
        membershipNumLabel.setText("Membership No: " + (m.membershipNumber() != null ? m.membershipNumber() : "-"));

        // Load Photo avatar
        if (m.photoPath() != null && !m.photoPath().isBlank()) {
            File photoFile = new File(m.photoPath());
            if (photoFile.exists()) {
                try (FileInputStream is = new FileInputStream(photoFile)) {
                    photoImageView.setImage(new Image(is));
                } catch (Exception ignored) {}
            }
        }

        // Demographics
        firstNameVal.setText(valOrDash(m.firstName()));
        middleNameVal.setText(valOrDash(m.middleName()));
        lastNameVal.setText(valOrDash(m.lastName()));
        genderVal.setText(valOrDash(m.gender()));
        dobVal.setText(valOrDash(m.dob()));
        occupationVal.setText(valOrDash(m.occupation()));
        mobileVal.setText(valOrDash(m.mobileNumber()));
        emailVal.setText(valOrDash(m.email()));
        emergencyNameVal.setText(valOrDash(m.emergencyContactName()));
        emergencyPhoneVal.setText(valOrDash(m.emergencyContactPhone()));

        // Classification
        memberTypeVal.setText(valOrDash(m.memberType()));
        admissionDateVal.setText(valOrDash(m.admissionDate()));
        resolutionNumVal.setText(valOrDash(m.resolutionNumber()));
        resolutionDateVal.setText(valOrDash(m.resolutionDate()));

        // Address
        permanentAddressVal.setText(valOrDash(m.permanentAddress()));
        correspondenceAddressVal.setText(valOrDash(m.correspondenceAddress()));

        // Joint Owners & Nominees
        if (m.jointOwners() != null) jointOwnerList.setAll(m.jointOwners());
        if (m.nominees() != null) nomineeList.setAll(m.nominees());

        // Documents
        aadhaarNumVal.setText(valOrDash(m.aadhaarNumber()));
        panNumVal.setText(valOrDash(m.panNumber()));

        setupDocumentButton(m.aadhaarDocPath(), aadhaarPathLabel, viewAadhaarBtn, downloadAadhaarBtn);
        setupDocumentButton(m.panDocPath(), panPathLabel, viewPanBtn, downloadPanBtn);
    }

    private void setupDocumentButton(String path, Label pathLabel, Button viewBtn, Button downloadBtn) {
        if (path != null && !path.isBlank() && new File(path).exists()) {
            pathLabel.setText(new File(path).getName());
            viewBtn.setDisable(false);
            downloadBtn.setDisable(false);
        } else {
            pathLabel.setText("No document uploaded");
            viewBtn.setDisable(true);
            downloadBtn.setDisable(true);
        }
    }

    private String valOrDash(String val) {
        return (val != null && !val.isBlank()) ? val : "-";
    }

    @FXML
    private void openAadhaarDoc() {
        openDocument(currentMember.aadhaarDocPath());
    }

    @FXML
    private void downloadAadhaarDoc() {
        downloadDocument(currentMember.aadhaarDocPath(), "Aadhaar");
    }

    @FXML
    private void openPanDoc() {
        openDocument(currentMember.panDocPath());
    }

    @FXML
    private void downloadPanDoc() {
        downloadDocument(currentMember.panDocPath(), "PAN");
    }

    private void openDocument(String docPath) {
        if (docPath == null || docPath.isBlank()) return;
        File file = new File(docPath);
        if (file.exists()) {
            try {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(file);
                } else {
                    showAlert(Alert.AlertType.INFORMATION, "File Location", "File is located at:\n" + file.getAbsolutePath());
                }
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error Opening File", ex.getMessage());
            }
        }
    }

    private void downloadDocument(String docPath, String docName) {
        if (docPath == null || docPath.isBlank()) return;
        File sourceFile = new File(docPath);
        if (!sourceFile.exists()) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save " + docName + " Document");
        fileChooser.setInitialFileName(sourceFile.getName());
        File targetFile = fileChooser.showSaveDialog(nameLabel.getScene().getWindow());

        if (targetFile != null) {
            try {
                Files.copy(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                showAlert(Alert.AlertType.INFORMATION, "Download Successful", "Saved document to:\n" + targetFile.getAbsolutePath());
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Download Failed", ex.getMessage());
            }
        }
    }

    @FXML
    private void backToMembers() {
        navigationManager.navigate(View.MEMBERS);
    }

    @FXML
    private void editMember() {
        navigationManager.navigate(View.MEMBER_REGISTRATION);
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
