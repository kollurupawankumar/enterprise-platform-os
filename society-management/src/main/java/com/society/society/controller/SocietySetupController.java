package com.society.society.controller;

import com.society.common.controller.BaseController;
import com.society.common.navigation.NavigationManager;
import com.society.common.navigation.View;
import com.society.society.context.SocietyContext;
import com.society.society.dto.SocietyDto;
import com.society.society.entity.FinancialYearStartMonth;
import com.society.society.service.SocietyService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Component;

@Component
public class SocietySetupController extends BaseController {

    private final SocietyService societyService;

    private final SocietyContext societyContext;

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

    public SocietySetupController(
            NavigationManager navigationManager,
            SocietyService societyService, SocietyContext societyContext) {

        super(navigationManager);
        this.societyService = societyService;
        this.societyContext = societyContext;
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

        SocietyDto dto = new SocietyDto(
                null,
                societyNameField.getText(),
                shortNameField.getText(),
                registrationNumberField.getText(),
                addressLine1Field.getText(),
                addressLine2Field.getText(),
                cityField.getText(),
                stateField.getText(),
                pinCodeField.getText(),
                phoneField.getText(),
                emailField.getText(),
                websiteField.getText(),
                financialYearCombo.getValue(),
                null,
                null,
                true
        );

        societyService.save(dto);

        societyContext.refresh();

        navigationManager.navigate(View.DASHBOARD);

    }

}