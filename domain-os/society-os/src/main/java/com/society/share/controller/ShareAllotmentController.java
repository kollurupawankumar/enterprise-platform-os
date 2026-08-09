package com.society.share.controller;

import com.society.common.controller.BaseController;
import com.society.common.navigation.NavigationManager;
import com.society.common.navigation.View;
import com.society.member.dto.MemberDto;
import com.society.member.service.MemberService;
import com.society.share.dto.ShareCertificateDto;
import com.society.share.service.ShareService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class ShareAllotmentController extends BaseController {

    private final ShareService shareService;
    private final MemberService memberService;

    @FXML
    private ComboBox<MemberDto> memberComboBox;

    @FXML
    private TextField certificateNumberField;

    @FXML
    private TextField fromShareNumberField;

    @FXML
    private TextField toShareNumberField;

    @FXML
    private TextField totalSharesField;

    @FXML
    private TextField faceValueField;

    @FXML
    private TextField totalAmountField;

    @FXML
    private DatePicker issueDatePicker;

    private boolean calculating = false;

    public ShareAllotmentController(
            NavigationManager navigationManager,
            ShareService shareService,
            MemberService memberService) {

        super(navigationManager);
        this.shareService = shareService;
        this.memberService = memberService;
    }

    @FXML
    public void initialize() {

        certificateNumberField.setText(shareService.generateCertificateNumber());
        certificateNumberField.setEditable(false);

        // Auto-detect next available starting share number
        int nextStartShare = shareService.getNextAvailableShareNumber();
        fromShareNumberField.setText(String.valueOf(nextStartShare));

        // Keep totalAmountField read-only as it's a computed sum
        totalAmountField.setEditable(false);

        faceValueField.setText("50.00");
        issueDatePicker.setValue(LocalDate.now());

        // Default standard share allocation (e.g. 10 shares)
        totalSharesField.setText("10");

        loadMembers();
        recalculateRangesAndTotals();

        totalSharesField.textProperty().addListener((obs, oldVal, newVal) -> recalculateRangesAndTotals());
        fromShareNumberField.textProperty().addListener((obs, oldVal, newVal) -> recalculateRangesAndTotals());
        toShareNumberField.textProperty().addListener((obs, oldVal, newVal) -> onToShareManualChanged());
        faceValueField.textProperty().addListener((obs, oldVal, newVal) -> recalculateTotalAmount());
    }

    private void loadMembers() {

        List<MemberDto> members = memberService.findAll();

        memberComboBox.getItems().setAll(members);

        memberComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(MemberDto item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String name = (item.firstName() + " " + (item.lastName() != null ? item.lastName() : "")).trim();
                    setText(name + " (" + item.memberNumber() + ")");
                }
            }
        });

        memberComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(MemberDto item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String name = (item.firstName() + " " + (item.lastName() != null ? item.lastName() : "")).trim();
                    setText(name + " (" + item.memberNumber() + ")");
                }
            }
        });

        if (!members.isEmpty()) {
            memberComboBox.getSelectionModel().select(0);
        }
    }

    private void recalculateRangesAndTotals() {
        if (calculating) return;
        calculating = true;

        try {
            String fromText = fromShareNumberField.getText().trim();
            String totalText = totalSharesField.getText().trim();

            if (!fromText.isEmpty() && !totalText.isEmpty()) {
                int from = Integer.parseInt(fromText);
                int total = Integer.parseInt(totalText);

                if (from > 0 && total > 0) {
                    int to = from + total - 1;
                    toShareNumberField.setText(String.valueOf(to));
                    recalculateTotalAmount();
                }
            }
        } catch (NumberFormatException ignored) {
        } finally {
            calculating = false;
        }
    }

    private void onToShareManualChanged() {
        if (calculating) return;
        calculating = true;

        try {
            String fromText = fromShareNumberField.getText().trim();
            String toText = toShareNumberField.getText().trim();

            if (!fromText.isEmpty() && !toText.isEmpty()) {
                int from = Integer.parseInt(fromText);
                int to = Integer.parseInt(toText);

                if (to >= from && from > 0) {
                    int total = to - from + 1;
                    totalSharesField.setText(String.valueOf(total));
                    recalculateTotalAmount();
                }
            }
        } catch (NumberFormatException ignored) {
        } finally {
            calculating = false;
        }
    }

    private void recalculateTotalAmount() {
        try {
            String totalText = totalSharesField.getText().trim();
            String faceValueText = faceValueField.getText().trim();

            if (!totalText.isEmpty() && !faceValueText.isEmpty()) {
                int total = Integer.parseInt(totalText);
                double faceValue = Double.parseDouble(faceValueText);

                if (total > 0 && faceValue >= 0) {
                    double totalAmount = total * faceValue;
                    totalAmountField.setText("₹ " + String.format("%.2f", totalAmount));
                    return;
                }
            }
        } catch (NumberFormatException ignored) {
        }
        totalAmountField.clear();
    }

    @FXML
    private void saveAllotment() {

        try {

            MemberDto selectedMember = memberComboBox.getValue();

            if (selectedMember == null) {
                showError("Please select a member for share allotment.");
                return;
            }

            int fromShare = Integer.parseInt(fromShareNumberField.getText().trim());
            int toShare = Integer.parseInt(toShareNumberField.getText().trim());
            double faceValue = Double.parseDouble(faceValueField.getText().trim());

            ShareCertificateDto dto = new ShareCertificateDto(
                    null,
                    certificateNumberField.getText(),
                    selectedMember.id(),
                    null,
                    null,
                    fromShare,
                    toShare,
                    null,
                    faceValue,
                    null,
                    issueDatePicker.getValue(),
                    null
            );

            shareService.allotShares(dto);

            showSuccess("Share certificate issued successfully!");

            navigationManager.navigate(View.SHARES);

        } catch (NumberFormatException ex) {

            showError("Please enter valid numeric values for Share Numbers and Face Value.");

        } catch (IllegalArgumentException ex) {

            showError(ex.getMessage());

        } catch (Exception ex) {

            showError("Unable to issue share certificate:\n" + ex.getMessage());

            ex.printStackTrace();

        }

    }

    @FXML
    private void cancel() {

        navigationManager.navigate(View.SHARES);

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
