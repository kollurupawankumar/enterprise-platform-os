package com.society.share.controller;

import com.society.common.controller.BaseController;
import com.society.common.navigation.NavigationManager;
import com.society.common.navigation.View;
import com.society.member.dto.MemberDto;
import com.society.member.service.MemberService;
import com.society.share.dto.ShareCertificateDto;
import com.society.share.service.ShareService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ShareController extends BaseController {

    private final ShareService shareService;
    private final MemberService memberService;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<ShareCertificateDto> shareTable;

    @FXML
    private TableColumn<ShareCertificateDto, String> certificateNumberColumn;

    @FXML
    private TableColumn<ShareCertificateDto, String> memberNameColumn;

    @FXML
    private TableColumn<ShareCertificateDto, String> shareRangeColumn;

    @FXML
    private TableColumn<ShareCertificateDto, String> totalSharesColumn;

    @FXML
    private TableColumn<ShareCertificateDto, String> totalAmountColumn;

    @FXML
    private TableColumn<ShareCertificateDto, String> issueDateColumn;

    @FXML
    private TableColumn<ShareCertificateDto, String> statusColumn;

    @FXML
    private Label totalCertificatesLabel;

    public ShareController(
            NavigationManager navigationManager,
            ShareService shareService,
            MemberService memberService) {

        super(navigationManager);
        this.shareService = shareService;
        this.memberService = memberService;
    }

    @FXML
    public void initialize() {

        configureTable();

        loadShareCertificates();

        shareTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY);

    }

    private void configureTable() {

        certificateNumberColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().certificateNumber()));

        memberNameColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().memberName()
                        + " (" + cell.getValue().memberNumber() + ")"));

        shareRangeColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().fromShareNumber()
                        + " - " + cell.getValue().toShareNumber()));

        totalSharesColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(String.valueOf(cell.getValue().totalShares())));

        totalAmountColumn.setCellValueFactory(cell ->
                new SimpleStringProperty("₹ " + String.format("%.2f", cell.getValue().totalAmount())));

        issueDateColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().issueDate() != null
                        ? cell.getValue().issueDate().toString()
                        : ""));

        statusColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().status().name()));

        statusColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    setText(item);
                    getStyleClass().removeAll("status-badge", "status-active", "status-inactive", "status-other");
                    getStyleClass().add("status-badge");
                    if ("ACTIVE".equalsIgnoreCase(item)) {
                        getStyleClass().add("status-active");
                    } else if ("TRANSFERRED".equalsIgnoreCase(item)) {
                        getStyleClass().add("status-other");
                    } else {
                        getStyleClass().add("status-inactive");
                    }
                }
            }
        });

    }

    private void loadShareCertificates() {

        List<ShareCertificateDto> certificates = shareService.findAll();

        shareTable.setItems(FXCollections.observableArrayList(certificates));

        totalCertificatesLabel.setText("Total Certificates : " + certificates.size());

    }

    @FXML
    private void allotShares() {

        navigationManager.navigate(View.SHARE_ALLOTMENT);

    }

    @FXML
    private void transferShares() {

        ShareCertificateDto selected = shareTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showInformation("Please select a share certificate to transfer.");
            return;
        }

        if ("TRANSFERRED".equalsIgnoreCase(selected.status().name())
                || "CANCELLED".equalsIgnoreCase(selected.status().name())) {
            showInformation("Only ACTIVE share certificates can be transferred.");
            return;
        }

        List<MemberDto> members = memberService.findAll()
                .stream()
                .filter(m -> !m.id().equals(selected.memberId()))
                .toList();

        if (members.isEmpty()) {
            showInformation("No other active members available to transfer shares to.");
            return;
        }

        ChoiceDialog<MemberDto> dialog = new ChoiceDialog<>(members.get(0), members);
        dialog.setTitle("Transfer Share Certificate");
        dialog.setHeaderText("Transfer Certificate " + selected.certificateNumber() + " (" + selected.memberName() + ")");
        dialog.setContentText("Select Target Member:");

        Optional<MemberDto> result = dialog.showAndWait();

        result.ifPresent(targetMember -> {
            try {
                shareService.transferShares(selected.id(), targetMember.id(), 0.0, "Share transfer");
                showInformation("Shares transferred successfully to " + targetMember.firstName() + "!");
                loadShareCertificates();
            } catch (Exception ex) {
                showInformation("Error during transfer: " + ex.getMessage());
            }
        });

    }

    @FXML
    private void searchShares() {

        String keyword = searchField.getText();

        List<ShareCertificateDto> certificates = shareService.search(keyword);

        shareTable.setItems(FXCollections.observableArrayList(certificates));

        totalCertificatesLabel.setText("Total Certificates : " + certificates.size());

    }

    @FXML
    private void refreshShares() {

        searchField.clear();

        loadShareCertificates();

    }

    @FXML
    private void printCertificate() {
        ShareCertificateDto selected = shareTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInformation("Please select a share certificate to print.");
            return;
        }

        javafx.print.PrinterJob job = javafx.print.PrinterJob.createPrinterJob();
        if (job != null) {
            boolean success = job.showPrintDialog(shareTable.getScene().getWindow());
            if (success) {
                Label printLabel = new Label(
                        "===========================================================\n" +
                        "                   SHARE CERTIFICATE (FORM I)              \n" +
                        "===========================================================\n" +
                        "Certificate No : " + selected.certificateNumber() + "\n" +
                        "Member Name    : " + selected.memberName() + "\n" +
                        "Share Range    : " + selected.fromShareNumber() + " to " + selected.toShareNumber() + "\n" +
                        "Total Shares   : " + selected.totalShares() + "\n" +
                        "Total Value    : ₹ " + selected.totalAmount() + "\n" +
                        "Issue Date     : " + selected.issueDate() + "\n" +
                        "===========================================================\n"
                );
                printLabel.setStyle("-fx-font-family: monospace; -fx-font-size: 14px; -fx-padding: 30px;");
                boolean printed = job.printPage(printLabel);
                if (printed) {
                    job.endJob();
                    showInformation("Share Certificate (Form I) printed successfully!");
                } else {
                    showInformation("Printing failed or was cancelled.");
                }
            }
        } else {
            showInformation("No printer found on this system.");
        }
    }

    private void showInformation(String message) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Share Management");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();

    }
}
