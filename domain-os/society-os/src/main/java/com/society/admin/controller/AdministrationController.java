package com.society.admin.controller;

import com.society.common.controller.BaseController;
import com.society.common.navigation.NavigationManager;
import com.society.society.entity.SocietyEntity;
import com.society.society.repository.SocietyRepository;
import com.society.user.entity.UserEntity;
import com.society.user.repository.UserRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
public class AdministrationController extends BaseController {

    private final SocietyRepository societyRepository;
    private final UserRepository userRepository;

    @FXML
    private TextField nameField;

    @FXML
    private TextField regNoField;

    @FXML
    private TextField shortNameField;

    @FXML
    private TextField memberFormatField;

    @FXML
    private TextField membershipFormatField;

    @FXML
    private TextField shareFormatField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField websiteField;

    @FXML
    private TextArea addressArea;

    @FXML
    private TableView<UserEntity> usersTable;

    @FXML
    private TableColumn<UserEntity, String> usernameCol;

    @FXML
    private TableColumn<UserEntity, String> firstNameCol;

    @FXML
    private TableColumn<UserEntity, String> roleCol;

    @FXML
    private TableColumn<UserEntity, String> emailCol;

    @FXML private ListView<String> backupListView;
    @FXML private TableView<com.society.operations.entity.AssetServiceLogEntity> auditTable;
    @FXML private TableColumn<com.society.operations.entity.AssetServiceLogEntity, String> auditEntityCol;
    @FXML private TableColumn<com.society.operations.entity.AssetServiceLogEntity, String> auditRecordCol;
    @FXML private TableColumn<com.society.operations.entity.AssetServiceLogEntity, String> auditCreatedByCol;
    @FXML private TableColumn<com.society.operations.entity.AssetServiceLogEntity, String> auditCreatedAtCol;
    @FXML private TableColumn<com.society.operations.entity.AssetServiceLogEntity, String> auditUpdatedByCol;
    @FXML private TableColumn<com.society.operations.entity.AssetServiceLogEntity, String> auditUpdatedAtCol;

    private final ObservableList<UserEntity> users = FXCollections.observableArrayList();
    private final ObservableList<com.society.operations.entity.AssetServiceLogEntity> auditLogs = FXCollections.observableArrayList();
    private SocietyEntity activeSociety;

    private final com.society.operations.repository.AssetServiceLogRepository assetServiceLogRepository;

    public AdministrationController(
            NavigationManager navigationManager,
            SocietyRepository societyRepository,
            UserRepository userRepository,
            com.society.operations.repository.AssetServiceLogRepository assetServiceLogRepository) {
        super(navigationManager);
        this.societyRepository = societyRepository;
        this.userRepository = userRepository;
        this.assetServiceLogRepository = assetServiceLogRepository;
    }

    @FXML
    public void initialize() {
        // Table Columns mapping
        usernameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsername()));
        firstNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFirstName() + " " + (c.getValue().getLastName() != null ? c.getValue().getLastName() : "")));
        roleCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRole()));
        emailCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail() != null ? c.getValue().getEmail() : "-"));

        if (auditTable != null) {
            auditEntityCol.setCellValueFactory(c -> new SimpleStringProperty("Asset Service Log"));
            auditRecordCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAsset() != null ? c.getValue().getAsset().getName() + " (" + c.getValue().getServiceType() + ")" : "-"));
            auditCreatedByCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCreatedBy() != null ? c.getValue().getCreatedBy() : "SYSTEM"));
            auditCreatedAtCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCreatedAt() != null ? c.getValue().getCreatedAt().toString().replace("T", " ") : "-"));
            auditUpdatedByCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUpdatedBy() != null ? c.getValue().getUpdatedBy() : "SYSTEM"));
            auditUpdatedAtCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUpdatedAt() != null ? c.getValue().getUpdatedAt().toString().replace("T", " ") : "-"));
        }

        loadSocietyDetails();
        loadUsers();
        loadBackups();
        loadAuditLogs();
    }

    @FXML
    private void handleRefreshAuditLogs() {
        loadAuditLogs();
    }

    private void loadAuditLogs() {
        if (auditTable != null && assetServiceLogRepository != null) {
            auditLogs.setAll(assetServiceLogRepository.findAllWithAsset());
            auditTable.setItems(auditLogs);
        }
    }

    private void loadSocietyDetails() {
        Optional<SocietyEntity> opt = societyRepository.findFirstByActiveTrue();
        if (opt.isPresent()) {
            activeSociety = opt.get();
            nameField.setText(activeSociety.getName());
            regNoField.setText(activeSociety.getRegistrationNumber());
            shortNameField.setText(activeSociety.getShortName());
            if (memberFormatField != null) {
                memberFormatField.setText(activeSociety.getMemberNumberFormat() != null ? activeSociety.getMemberNumberFormat() : "MEM-{SEQ}");
            }
            if (membershipFormatField != null) {
                membershipFormatField.setText(activeSociety.getMembershipNumberFormat() != null ? activeSociety.getMembershipNumberFormat() : "SSTS/{YEAR}/{SEQ}");
            }
            if (shareFormatField != null) {
                shareFormatField.setText(activeSociety.getShareCertificateFormat() != null ? activeSociety.getShareCertificateFormat() : "SC/{YEAR}/{SEQ}");
            }
            emailField.setText(activeSociety.getEmail());
            phoneField.setText(activeSociety.getPhone());
            websiteField.setText(activeSociety.getWebsite());
            addressArea.setText((activeSociety.getAddressLine1() + "\n" + (activeSociety.getAddressLine2() != null ? activeSociety.getAddressLine2() : "")).trim());
        }
    }

    private void loadUsers() {
        users.setAll(userRepository.findAll());
        usersTable.setItems(users);
    }

    @FXML
    private void handleSaveSocietyDetails() {
        if (activeSociety == null) return;

        activeSociety.setName(nameField.getText().trim());
        activeSociety.setRegistrationNumber(regNoField.getText().trim());
        activeSociety.setShortName(shortNameField.getText().trim());
        if (memberFormatField != null) {
            activeSociety.setMemberNumberFormat(memberFormatField.getText().trim());
        }
        if (membershipFormatField != null) {
            activeSociety.setMembershipNumberFormat(membershipFormatField.getText().trim());
        }
        if (shareFormatField != null) {
            activeSociety.setShareCertificateFormat(shareFormatField.getText().trim());
        }
        activeSociety.setEmail(emailField.getText().trim());
        activeSociety.setPhone(phoneField.getText().trim());
        activeSociety.setWebsite(websiteField.getText().trim());

        String[] lines = addressArea.getText().split("\n", 2);
        activeSociety.setAddressLine1(lines[0].trim());
        if (lines.length > 1) {
            activeSociety.setAddressLine2(lines[1].trim());
        } else {
            activeSociety.setAddressLine2("");
        }

        societyRepository.save(activeSociety);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Saved");
        alert.setHeaderText(null);
        alert.setContentText("Cooperative Housing Society settings updated successfully.");
        alert.showAndWait();
    }

    @FXML
    private void handleAddUser() {
        Dialog<UserEntity> dialog = new Dialog<>();
        dialog.setTitle("Create System User");
        dialog.setHeaderText("Enter details for the new user profile:");

        ButtonType saveButtonType = new ButtonType("Save User", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField username = new TextField();
        username.setPromptText("e.g. jdoe");
        PasswordField password = new PasswordField();
        password.setPromptText("Initial password");
        TextField firstName = new TextField();
        firstName.setPromptText("First Name");
        TextField lastName = new TextField();
        lastName.setPromptText("Last Name");
        TextField email = new TextField();
        email.setPromptText("email@society.org");

        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("ADMINISTRATOR", "PRESIDENT", "SECRETARY", "TREASURER", "AUDITOR", "MEMBER");
        roleCombo.setValue("MEMBER");

        grid.add(new Label("Username:"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(password, 1, 1);
        grid.add(new Label("First Name:"), 0, 2);
        grid.add(firstName, 1, 2);
        grid.add(new Label("Last Name:"), 0, 3);
        grid.add(lastName, 1, 3);
        grid.add(new Label("Email:"), 0, 4);
        grid.add(email, 1, 4);
        grid.add(new Label("Role:"), 0, 5);
        grid.add(roleCombo, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                UserEntity u = new UserEntity();
                u.setUsername(username.getText().trim());
                u.setPasswordHash(BCrypt.hashpw(password.getText().trim(), BCrypt.gensalt()));
                u.setFirstName(firstName.getText().trim());
                u.setLastName(lastName.getText().trim());
                u.setEmail(email.getText().trim());
                u.setRole(roleCombo.getValue());
                u.setActive(true);
                return u;
            }
            return null;
        });

        Optional<UserEntity> result = dialog.showAndWait();
        result.ifPresent(u -> {
            userRepository.save(u);
            loadUsers();
        });
    }

    private void loadBackups() {
        File backupDir = new File("backups");
        if (backupDir.exists() && backupDir.isDirectory()) {
            File[] files = backupDir.listFiles((dir, name) -> name.endsWith(".zip") || name.endsWith(".db"));
            if (files != null) {
                ObservableList<String> items = FXCollections.observableArrayList();
                for (File f : files) {
                    items.add(f.getName());
                }
                backupListView.setItems(items);
            }
        }
    }

    @FXML
    private void handleTriggerBackup() {
        File dbFile = new File("society.db");
        if (!dbFile.exists()) {
            showError("No active SQLite database file (society.db) found to backup.");
            return;
        }

        File backupDir = new File("backups");
        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        File destFile = new File(backupDir, "society_backup_" + timestamp + ".db");

        try {
            Files.copy(dbFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            showInfo("Backup Created", "Successfully created local database snapshot:\n" + destFile.getName());
            loadBackups();
        } catch (IOException e) {
            showError("Backup Failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleRestoreBackup() {
        String selected = backupListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a restore point from the list.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Restore");
        confirm.setHeaderText("Database Restoration Warning");
        confirm.setContentText("Restoring " + selected + " will replace current data. Proceed?");
        Optional<ButtonType> res = confirm.showAndWait();

        if (res.isPresent() && res.get() == ButtonType.OK) {
            File source = new File("backups", selected);
            File dest = new File("society.db");
            try {
                Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                showInfo("Restore Complete", "Database restored successfully. Please restart Society OS to apply changes.");
            } catch (IOException e) {
                showError("Restore Failed: " + e.getMessage());
            }
        }
    }

    private void showInfo(String title, String content) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(content);
        a.showAndWait();
    }

    private void showError(String content) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText(null);
        a.setContentText(content);
        a.showAndWait();
    }
}
