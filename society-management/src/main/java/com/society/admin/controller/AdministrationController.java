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
            auditLogs.setAll(assetServiceLogRepository.findAll());
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
        dialog.setHeaderText("Specify login credentials:");

        ButtonType createBtnType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createBtnType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField userField = new TextField();
        PasswordField passField = new PasswordField();
        TextField firstField = new TextField();
        TextField lastField = new TextField();
        TextField emailAddressField = new TextField();
        ComboBox<String> roleCombo = new ComboBox<>(FXCollections.observableArrayList("ADMINISTRATOR", "SECRETARY", "TREASURER", "PRESIDENT", "AUDITOR", "OFFICE_ASSISTANT"));
        roleCombo.getSelectionModel().selectFirst();

        grid.add(new Label("Username:"), 0, 0);
        grid.add(userField, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(passField, 1, 1);
        grid.add(new Label("First Name:"), 0, 2);
        grid.add(firstField, 1, 2);
        grid.add(new Label("Last Name:"), 0, 3);
        grid.add(lastField, 1, 3);
        grid.add(new Label("Email:"), 0, 4);
        grid.add(emailAddressField, 1, 4);
        grid.add(new Label("System Role:"), 0, 5);
        grid.add(roleCombo, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createBtnType) {
                UserEntity u = new UserEntity();
                u.setUsername(userField.getText().trim());
                u.setPasswordHash(BCrypt.hashpw(passField.getText().trim(), BCrypt.gensalt()));
                u.setFirstName(firstField.getText().trim());
                u.setLastName(lastField.getText().trim());
                u.setEmail(emailAddressField.getText().trim());
                u.setRole(roleCombo.getValue());
                u.setActive(true);
                return u;
            }
            return null;
        });

        Optional<UserEntity> result = dialog.showAndWait();
        result.ifPresent(u -> {
            if (!u.getUsername().isEmpty() && !u.getFirstName().isEmpty()) {
                userRepository.save(u);
                loadUsers();
            }
        });
    }

    private void loadBackups() {
        File dir = new File("backups");
        if (!dir.exists()) {
            dir.mkdir();
        }
        ObservableList<String> list = FXCollections.observableArrayList();
        File[] files = dir.listFiles((d, name) -> name.endsWith(".db"));
        if (files != null) {
            for (File f : files) {
                list.add(f.getName());
            }
        }
        backupListView.setItems(list);
    }

    @FXML
    private void handleTriggerBackup() {
        File src = new File("database/society.db");
        if (!src.exists()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Backup Failed");
            alert.setHeaderText(null);
            alert.setContentText("Source SQLite database file 'database/society.db' not found.");
            alert.showAndWait();
            return;
        }

        File dir = new File("backups");
        if (!dir.exists()) {
            dir.mkdir();
        }

        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        File dest = new File(dir, "backup_" + ts + ".db");

        try {
            Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Backup Completed");
            alert.setHeaderText(null);
            alert.setContentText("Database snapshot successfully copied to " + dest.getPath());
            alert.showAndWait();
            loadBackups();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Backup Failed");
            alert.setHeaderText(null);
            alert.setContentText("Error creating database backup: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleRestoreBackup() {
        String selected = backupListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select a restore point from the list first.");
            alert.showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Restore");
        confirm.setHeaderText("Caution: Restoring will overwrite current database!");
        confirm.setContentText("Are you sure you want to restore the selected database snapshot?");
        Optional<ButtonType> click = confirm.showAndWait();

        if (click.isPresent() && click.get() == ButtonType.OK) {
            File src = new File("backups", selected);
            File dest = new File("database/society.db");

            try {
                Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Restore Completed");
                alert.setHeaderText(null);
                alert.setContentText("Database successfully restored. Please restart the application to apply all changes.");
                alert.showAndWait();
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Restore Failed");
                alert.setHeaderText(null);
                alert.setContentText("Error restoring database snapshot: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }
}
