package com.society.member.controller;

import com.society.common.controller.BaseController;
import com.society.common.navigation.NavigationManager;
import com.society.common.navigation.View;
import com.society.member.dto.MemberDto;
import com.society.member.entity.MemberStatus;
import com.society.member.service.MemberService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Component;

@Component
public class MemberRegistrationController extends BaseController {

    private final MemberService memberService;

    @FXML
    private TextField memberNumberField;

    @FXML
    private TextField membershipNumberField;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField mobileNumberField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField aadhaarNumberField;

    @FXML
    private TextField panNumberField;

    @FXML
    private ComboBox<String> statusComboBox;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    public MemberRegistrationController(
            NavigationManager navigationManager,
            MemberService memberService) {

        super(navigationManager);
        this.memberService = memberService;
    }

    @FXML
    public void initialize() {

        memberNumberField.setEditable(false);

        statusComboBox.getItems().setAll(
                "ACTIVE",
                "INACTIVE",
                "TRANSFERRED",
                "RESIGNED",
                "DECEASED"
        );

        statusComboBox.getSelectionModel().select("ACTIVE");

        loadGeneratedMemberNumber();

    }

    private void loadGeneratedMemberNumber() {

        memberNumberField.setText(
                memberService.generateMemberNumber());

    }

    @FXML
    private void save() {

        if (firstNameField.getText().isBlank()) {

            showError("First Name is mandatory.");

            return;

        }

        if (mobileNumberField.getText().isBlank()) {

            showError("Mobile Number is mandatory.");

            return;

        }

        MemberDto dto = new MemberDto(

                null,

                memberNumberField.getText(),

                membershipNumberField.getText(),

                firstNameField.getText(),

                lastNameField.getText(),

                mobileNumberField.getText(),

                emailField.getText(),

                aadhaarNumberField.getText(),

                panNumberField.getText(),

                MemberStatus.valueOf(
                        statusComboBox.getValue()),

                true

        );

        memberService.save(dto);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setHeaderText(null);

        alert.setTitle("Success");

        alert.setContentText("Member saved successfully.");

        alert.showAndWait();

        navigationManager.navigate(View.MEMBERS);

    }

    @FXML
    private void cancel() {

        navigationManager.navigate(View.MEMBERS);

    }

    private void showError(String message) {

        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setHeaderText(null);

        alert.setTitle("Validation");

        alert.setContentText(message);

        alert.showAndWait();

    }

}