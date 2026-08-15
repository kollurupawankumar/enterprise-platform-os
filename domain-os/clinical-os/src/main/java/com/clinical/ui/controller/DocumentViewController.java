package com.clinical.ui.controller;

import com.clinical.document.entity.PatientDocumentEntity;
import com.clinical.document.service.PatientDocumentService;
import com.clinical.patient.entity.PatientEntity;
import com.clinical.patient.service.PatientService;
import com.clinical.visit.entity.VisitEntity;
import com.clinical.visit.service.VisitService;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import org.springframework.stereotype.Component;

import java.awt.Desktop;
import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class DocumentViewController {

    private final PatientDocumentService documentService;
    private final PatientService patientService;
    private final VisitService visitService;

    // FXML Controls
    @FXML private ComboBox<String> patientCombo;
    @FXML private ComboBox<String> categoryFilterCombo;

    @FXML private TableView<PatientDocumentEntity> documentTable;
    @FXML private TableColumn<PatientDocumentEntity, String> colDocId;
    @FXML private TableColumn<PatientDocumentEntity, String> colPatient;
    @FXML private TableColumn<PatientDocumentEntity, String> colVisit;
    @FXML private TableColumn<PatientDocumentEntity, String> colDocName;
    @FXML private TableColumn<PatientDocumentEntity, String> colType;
    @FXML private TableColumn<PatientDocumentEntity, String> colSize;
    @FXML private TableColumn<PatientDocumentEntity, String> colUploadedBy;
    @FXML private TableColumn<PatientDocumentEntity, String> colDate;
    @FXML private TableColumn<PatientDocumentEntity, String> colRemarks;

    private final ObservableList<PatientDocumentEntity> documentList = FXCollections.observableArrayList();
    private static final String[] CATEGORIES = {"ALL", "X-RAY", "MRI", "USG", "EXTERNAL_LAB_REPORT", "DISCHARGE_SUMMARY", "OTHER"};

    public DocumentViewController(PatientDocumentService documentService,
                                  PatientService patientService,
                                  VisitService visitService) {
        this.documentService = documentService;
        this.patientService = patientService;
        this.visitService = visitService;
    }

    @FXML
    public void initialize() {
        if (categoryFilterCombo != null) {
            categoryFilterCombo.setItems(FXCollections.observableArrayList(CATEGORIES));
            categoryFilterCombo.setValue("ALL");
            categoryFilterCombo.valueProperty().addListener((obs, oldVal, newVal) -> handleRefreshDocuments());
        }

        loadPatientCombo();
        setupTable();
        handleRefreshDocuments();
    }

    private void loadPatientCombo() {
        if (patientCombo == null) return;
        List<PatientEntity> patients = patientService.getAllPatients();
        ObservableList<String> options = FXCollections.observableArrayList();
        options.add("ALL - All Patient Charts");
        for (PatientEntity p : patients) {
            options.add(p.getPatientId() + " - " + p.getFirstName() + " " + p.getLastName());
        }
        patientCombo.setItems(options);
        patientCombo.setValue("ALL - All Patient Charts");
        patientCombo.valueProperty().addListener((obs, oldVal, newVal) -> handleRefreshDocuments());
    }

    private void setupTable() {
        if (documentTable == null) return;
        colDocId.setCellValueFactory(new PropertyValueFactory<>("documentId"));
        colPatient.setCellValueFactory(new PropertyValueFactory<>("patientId"));
        colVisit.setCellValueFactory(new PropertyValueFactory<>("visitId"));
        colDocName.setCellValueFactory(new PropertyValueFactory<>("documentName"));
        colType.setCellValueFactory(new PropertyValueFactory<>("documentType"));
        colSize.setCellValueFactory(new PropertyValueFactory<>("fileSize"));
        colUploadedBy.setCellValueFactory(new PropertyValueFactory<>("uploadedBy"));
        colDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCreatedAt() != null ? data.getValue().getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "-"));
        colRemarks.setCellValueFactory(new PropertyValueFactory<>("remarks"));

        documentTable.setItems(documentList);
    }

    @FXML
    public void handleRefreshDocuments() {
        String pSel = patientCombo != null && patientCombo.getValue() != null ? patientCombo.getValue() : "ALL";
        String pId = pSel.split(" - ")[0].trim();

        List<PatientDocumentEntity> docs;
        if ("ALL".equalsIgnoreCase(pId)) {
            docs = documentService.getAllDocuments();
        } else {
            docs = documentService.getDocumentsByPatient(pId);
        }

        String catFilter = categoryFilterCombo != null ? categoryFilterCombo.getValue() : "ALL";
        if (catFilter != null && !"ALL".equalsIgnoreCase(catFilter)) {
            docs = docs.stream().filter(d -> catFilter.equalsIgnoreCase(d.getDocumentType())).toList();
        }

        documentList.setAll(docs);
    }

    @FXML
    public void handleOpenUploadDialog() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Medical File to Upload");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Supported Medical Files", "*.pdf", "*.png", "*.jpg", "*.jpeg", "*.dcm"),
                new FileChooser.ExtensionFilter("PDF Documents", "*.pdf"),
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(documentTable != null ? documentTable.getScene().getWindow() : null);
        if (selectedFile == null) return;

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Attach Medical Record to Patient Chart");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        ComboBox<String> pCombo = new ComboBox<>();
        List<PatientEntity> patients = patientService.getAllPatients();
        ObservableList<String> pOptions = FXCollections.observableArrayList();
        for (PatientEntity p : patients) {
            pOptions.add(p.getPatientId() + " - " + p.getFirstName() + " " + p.getLastName());
        }
        pCombo.setItems(pOptions);
        pCombo.setPrefWidth(260);
        if (!pOptions.isEmpty()) pCombo.getSelectionModel().select(0);

        ComboBox<String> vCombo = new ComboBox<>();
        List<VisitEntity> visits = visitService.getAllVisits();
        ObservableList<String> vOptions = FXCollections.observableArrayList();
        vOptions.add("GENERAL-CHARTS - General Patient History");
        for (VisitEntity v : visits) {
            vOptions.add(v.getVisitId() + " - " + v.getVisitDate());
        }
        vCombo.setItems(vOptions);
        vCombo.setValue("GENERAL-CHARTS - General Patient History");
        vCombo.setPrefWidth(260);

        TextField nameField = new TextField(selectedFile.getName());
        ComboBox<String> typeCombo = new ComboBox<>(FXCollections.observableArrayList("X-RAY", "MRI", "USG", "EXTERNAL_LAB_REPORT", "DISCHARGE_SUMMARY", "OTHER"));
        typeCombo.setValue("EXTERNAL_LAB_REPORT");

        TextField remarksField = new TextField();
        remarksField.setPromptText("Clinical remarks or tags");

        grid.add(new Label("Selected File:"), 0, 0);
        grid.add(new Label(selectedFile.getName() + " (" + (selectedFile.length() / 1024) + " KB)"), 1, 0);
        grid.add(new Label("Target Patient:"), 0, 1);
        grid.add(pCombo, 1, 1);
        grid.add(new Label("Linked Visit (Optional):"), 0, 2);
        grid.add(vCombo, 1, 2);
        grid.add(new Label("Document Title:"), 0, 3);
        grid.add(nameField, 1, 3);
        grid.add(new Label("Document Category:"), 0, 4);
        grid.add(typeCombo, 1, 4);
        grid.add(new Label("Remarks / Tags:"), 0, 5);
        grid.add(remarksField, 1, 5);

        dialog.getDialogPane().setContent(grid);
        ButtonType uploadBtnType = new ButtonType("Upload File", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(uploadBtnType, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(btn -> {
            if (btn == uploadBtnType && pCombo.getValue() != null) {
                String pId = pCombo.getValue().split(" - ")[0].trim();
                String vId = vCombo.getValue() != null ? vCombo.getValue().split(" - ")[0].trim() : "GENERAL-CHARTS";
                try {
                    PatientDocumentEntity uploaded = documentService.uploadDocument(
                            pId, vId, nameField.getText(), typeCombo.getValue(), selectedFile, remarksField.getText(), "Reception / Staff"
                    );
                    handleRefreshDocuments();
                    showAlert(Alert.AlertType.INFORMATION, "File Attached", "Medical document " + uploaded.getDocumentId() + " attached to patient chart successfully!");
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Upload Error", "Failed to store document: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    public void handleOpenFile() {
        PatientDocumentEntity selected = documentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Document Selected", "Please select a document from the table to view.");
            return;
        }

        try {
            File f = new File(selected.getFilePath());
            if (f.exists()) {
                Desktop.getDesktop().open(f);
            } else {
                showAlert(Alert.AlertType.ERROR, "File Not Found", "The document file was not found on local disk: " + selected.getFilePath());
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Open Error", "Unable to open document: " + e.getMessage());
        }
    }

    @FXML
    public void handleDeleteDocument() {
        PatientDocumentEntity selected = documentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Document Selected", "Please select a document to delete.");
            return;
        }

        documentService.deleteDocument(selected.getDocumentId());
        handleRefreshDocuments();
        showAlert(Alert.AlertType.INFORMATION, "Document Deleted", "Document " + selected.getDocumentId() + " removed from patient chart.");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
