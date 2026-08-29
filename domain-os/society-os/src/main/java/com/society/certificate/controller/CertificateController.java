package com.society.certificate.controller;

import com.society.app.FXMLLoaderFactory;
import com.society.common.controller.BaseController;
import com.society.common.navigation.NavigationManager;
import com.society.member.dto.MemberDto;
import com.society.member.service.MemberService;
import com.society.property.service.PropertyService;
import com.society.share.dto.ShareCertificateDto;
import com.society.share.service.ShareService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CertificateController extends BaseController {

    private final ShareService shareService;
    private final MemberService memberService;
    private final PropertyService propertyService;
    private final FXMLLoaderFactory loaderFactory;

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

    public CertificateController(
            NavigationManager navigationManager,
            ShareService shareService,
            MemberService memberService,
            PropertyService propertyService,
            FXMLLoaderFactory loaderFactory) {

        super(navigationManager);
        this.shareService = shareService;
        this.memberService = memberService;
        this.propertyService = propertyService;
        this.loaderFactory = loaderFactory;
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
    private void printCertificate() {

        ShareCertificateDto selected = shareTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showInformation("Please select a share certificate from the table to print.");
            return;
        }

        try {

            FXMLLoader loader = loaderFactory.create("/fxml/certificate/share-certificate-print.fxml");
            Parent root = loader.load();

            PrintPreviewController previewController = loader.getController();

            // Fetch member details & property
            MemberDto member = memberService.findById(selected.memberId());
            List<com.society.property.entity.PropertyEntity> props = propertyService.getPropertiesByOwner(selected.memberId());
            String propNo = (props != null && !props.isEmpty()) ? props.get(0).getPropertyNumber() : null;

            previewController.setCertificateData(selected, member, propNo);

            // Open in a modal window
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Print Preview - Share Certificate " + selected.certificateNumber());
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(shareTable.getScene().getWindow());

            // Build layout with printable node, Print button, and Save PDF button
            javafx.scene.layout.HBox btnBox = new javafx.scene.layout.HBox(15);
            btnBox.setAlignment(javafx.geometry.Pos.CENTER);

            Button printBtn = new Button("🖨 Print Certificate");
            printBtn.getStyleClass().add("button-primary");
            printBtn.setOnAction(e -> previewController.print());

            Button savePdfBtn = new Button("💾 Save as PDF / HTML");
            savePdfBtn.getStyleClass().add("button-secondary");
            savePdfBtn.setOnAction(e -> previewController.saveHtmlPdf());

            btnBox.getChildren().addAll(printBtn, savePdfBtn);

            javafx.scene.layout.VBox layout = new javafx.scene.layout.VBox(10);
            layout.setPadding(new Insets(10));
            layout.setAlignment(javafx.geometry.Pos.CENTER);
            layout.setStyle("-fx-background-color: #E2E8F0;");

            layout.getChildren().addAll(root, btnBox);

            Scene scene = new Scene(layout);
            scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());
            dialogStage.setScene(scene);

            dialogStage.showAndWait();

        } catch (Exception ex) {

            showError("Unable to open print preview:\n" + ex.getMessage());
            ex.printStackTrace();

        }

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

    private void showInformation(String message) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();

    }

    private void showError(String message) {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();

    }
}
