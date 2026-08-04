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

    private final ObservableList<UserEntity> users = FXCollections.observableArrayList();
    private SocietyEntity activeSociety;

    public AdministrationController(
            NavigationManager navigationManager,
            SocietyRepository societyRepository,
            UserRepository userRepository) {
        super(navigationManager);
        this.societyRepository = societyRepository;
        this.userRepository = userRepository;
    }

    @FXML
    public void initialize() {
        // Table Columns mapping
        usernameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsername()));
        firstNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFirstName() + " " + (c.getValue().getLastName() != null ? c.getValue().getLastName() : "")));
        roleCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRole()));
        emailCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail() != null ? c.getValue().getEmail() : "-"));

        loadSocietyDetails();
        loadUsers();
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
}
