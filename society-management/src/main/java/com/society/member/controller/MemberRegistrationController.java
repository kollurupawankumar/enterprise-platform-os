package com.society.member.controller;

import com.society.common.controller.BaseController;
import com.society.common.navigation.NavigationManager;
import com.society.common.navigation.View;
import com.society.member.context.MemberContext;
import com.society.member.dto.MemberDto;
import com.society.member.entity.MemberStatus;
import com.society.member.service.MemberService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Component;

@Component
public class MemberRegistrationController extends BaseController {

    private final MemberService memberService;
    private final MemberContext memberContext;

    @FXML
    private Label titleLabel;

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
            MemberService memberService,
            MemberContext memberContext) {

        super(navigationManager);
        this.memberService = memberService;
        this.memberContext = memberContext;
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

        if (memberContext.isEditMode()) {
            MemberDto selected = memberContext.getSelectedMember();
            if (titleLabel != null) {
                titleLabel.setText("Edit Member Details");
            }
            memberNumberField.setText(selected.memberNumber());
            membershipNumberField.setText(selected.membershipNumber());
            firstNameField.setText(selected.firstName());
            lastNameField.setText(selected.lastName());
            mobileNumberField.setText(selected.mobileNumber());
            emailField.setText(selected.email());
            aadhaarNumberField.setText(selected.aadhaarNumber());
            panNumberField.setText(selected.panNumber());
            if (selected.status() != null) {
                statusComboBox.getSelectionModel().select(selected.status().name());
            } else {
                statusComboBox.getSelectionModel().select(MemberStatus.ACTIVE.name());
            }
        } else {
            if (titleLabel != null) {
                titleLabel.setText("Register Member");
            }
            statusComboBox.getSelectionModel().select(MemberStatus.ACTIVE.name());
            loadGeneratedMemberNumber();
        }
    }

    private void loadGeneratedMemberNumber() {

        memberNumberField.setText(
                memberService.generateMemberNumber()
        );
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

        return new MemberDto(

                id,

                memberNumberField.getText(),

                membershipNumberField.getText() == null || membershipNumberField.getText().isBlank()
                        ? null
                        : membershipNumberField.getText().trim(),

                firstNameField.getText() == null
                        ? null
                        : firstNameField.getText().trim(),

                lastNameField.getText() == null
                        ? null
                        : lastNameField.getText().trim(),

                mobileNumberField.getText() == null
                        ? null
                        : mobileNumberField.getText().trim(),

                emailField.getText() == null
                        ? null
                        : emailField.getText().trim(),

                aadhaarNumberField.getText() == null
                        ? null
                        : aadhaarNumberField.getText().trim(),

                panNumberField.getText() == null
                        ? null
                        : panNumberField.getText().trim(),

                status,

                true

        );

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