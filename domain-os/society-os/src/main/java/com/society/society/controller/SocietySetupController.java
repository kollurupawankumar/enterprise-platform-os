package com.society.society.controller;

import com.society.common.controller.BaseController;
import com.society.common.navigation.NavigationManager;
import com.society.common.navigation.View;
import com.society.society.dto.SocietyDto;
import com.society.society.entity.FinancialYearStartMonth;
import com.society.society.runtime.SocietyProvider;
import com.society.society.service.SocietyService;
import com.society.user.context.UserContext;
import com.society.user.entity.UserEntity;
import com.society.user.service.UserService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Component;

@Component
public class SocietySetupController extends BaseController {

    private final SocietyService societyService;
    private final SocietyProvider societyProvider;
    private final UserService userService;
    private final UserContext userContext;

    @FXML
    private TextField societyNameField;

    @FXML
    private TextField shortNameField;

    @FXML
    private TextField registrationNumberField;

    @FXML
    private TextField addressLine1Field;

    @FXML
    private TextField addressLine2Field;

    @FXML
    private TextField cityField;

    @FXML
    private TextField stateField;

    @FXML
    private TextField pinCodeField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField websiteField;

    @FXML
    private ComboBox<FinancialYearStartMonth> financialYearCombo;

    @FXML
    private TextField adminUsernameField;

    @FXML
    private PasswordField adminPasswordField;

    @FXML
    private TextField adminFirstNameField;

    @FXML
    private TextField adminLastNameField;

    @FXML
    private TextField adminEmailField;

    public SocietySetupController(
            NavigationManager navigationManager,
            SocietyService societyService,
            SocietyProvider societyProvider,
            UserService userService,
            UserContext userContext) {

        super(navigationManager);

        this.societyService = societyService;
        this.societyProvider = societyProvider;
        this.userService = userService;
        this.userContext = userContext;
    }

    @FXML
    public void initialize() {

        financialYearCombo.setItems(
                FXCollections.observableArrayList(
                        FinancialYearStartMonth.values()));

        financialYearCombo.getSelectionModel()
                .select(FinancialYearStartMonth.APRIL);
    }

    @FXML
    private void save() {
        String adminUsername = adminUsernameField.getText().trim();
        String adminPassword = adminPasswordField.getText();
        String adminFirstName = adminFirstNameField.getText().trim();

        if (adminUsername.isEmpty() || adminPassword.isEmpty() || adminFirstName.isEmpty()) {
            // Log/Notify validation (In production we should use a proper dialog, for now print or throw)
            throw new IllegalArgumentException("Admin Username, Password and First Name are required.");
        }

        SocietyDto dto = new SocietyDto(
                societyNameField.getText().trim(),
                shortNameField.getText().trim(),
                registrationNumberField.getText().trim(),
                addressLine1Field.getText().trim(),
                addressLine2Field.getText().trim(),
                cityField.getText().trim(),
                stateField.getText().trim(),
                pinCodeField.getText().trim(),
                phoneField.getText().trim(),
                emailField.getText().trim(),
                websiteField.getText().trim(),
                financialYearCombo.getValue(),
                "ICSC/{YEAR}/{SEQ}",
                "SC/{YEAR}/{SEQ}",
                null,
                null,
                true
        );

        societyService.save(dto);

        // Save admin user
        UserEntity adminUser = userService.createUser(
                adminUsername,
                adminPassword,
                "ADMINISTRATOR",
                adminFirstName,
                adminLastNameField.getText().trim(),
                adminEmailField.getText().trim()
        );

        // Log user in
        userContext.setCurrentUser(adminUser);

        societyProvider.refresh();

        navigationManager.navigate(View.DASHBOARD);
    }
}