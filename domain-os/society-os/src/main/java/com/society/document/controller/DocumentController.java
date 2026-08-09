package com.society.document.controller;

import com.society.common.controller.BaseController;
import com.society.common.navigation.NavigationManager;
import com.society.document.entity.DocumentEntity;
import com.society.document.repository.DocumentRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class DocumentController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(DocumentController.class);

    private final DocumentRepository documentRepository;

    @FXML
    private ComboBox<String> categoryFilterCombo;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<DocumentEntity> documentTable;

    @FXML
    private TableColumn<DocumentEntity, String> titleCol;

    @FXML
    private TableColumn<DocumentEntity, String> categoryCol;

    @FXML
    private TableColumn<DocumentEntity, String> tagsCol;

    @FXML
    private TableColumn<DocumentEntity, String> sizeCol;

    @FXML
    private TableColumn<DocumentEntity, String> expiryCol;

    @FXML
    private TableColumn<DocumentEntity, Void> actionCol;

    private final ObservableList<DocumentEntity> documents = FXCollections.observableArrayList();

    public DocumentController(NavigationManager navigationManager, DocumentRepository documentRepository) {
        super(navigationManager);
        this.documentRepository = documentRepository;
    }

    @FXML
    public void initialize() {
        categoryFilterCombo.setItems(FXCollections.observableArrayList(
                "ALL", "GOVERNANCE", "COMPLIANCE", "FINANCE", "LEGAL", "GENERAL"
        ));
        categoryFilterCombo.getSelectionModel().selectFirst();

        titleCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitle()));
        categoryCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategory()));
        tagsCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTags() != null ? c.getValue().getTags() : "-"));
        sizeCol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getFileSize() / 1024)));
        expiryCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getExpiryDate() != null ? c.getValue().getExpiryDate() : "-"));

        setupActionColumn();
        loadDocuments();
    }

    private void loadDocuments() {
        documents.setAll(documentRepository.findAll());
        documentTable.setItems(documents);
    }

    private void setupActionColumn() {
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button deleteBtn = new Button("Delete");

            {
                deleteBtn.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-font-weight: bold;");
                deleteBtn.setOnAction(e -> {
                    DocumentEntity doc = getTableView().getItems().get(getIndex());
                    documentRepository.delete(doc);
                    // Also delete local file if it exists
                    File f = new File(doc.getFilePath());
                    if (f.exists()) {
                        f.delete();
                    }
                    loadDocuments();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteBtn);
                }
            }
        });
    }

    @FXML
    private void handleFilter() {
        String cat = categoryFilterCombo.getValue();
        String query = searchField.getText().toLowerCase().trim();

        List<DocumentEntity> filtered = documentRepository.findAll().stream()
                .filter(d -> cat.equals("ALL") || d.getCategory().equalsIgnoreCase(cat))
                .filter(d -> query.isEmpty() || d.getTitle().toLowerCase().contains(query) || (d.getTags() != null && d.getTags().toLowerCase().contains(query)))
                .collect(Collectors.toList());

        documents.setAll(filtered);
    }

    @FXML
    private void handleUploadDocument() {
        Dialog<DocumentEntity> dialog = new Dialog<>();
        dialog.setTitle("Upload Document Metadata");
        dialog.setHeaderText("Register new document metadata:");

        ButtonType uploadBtnType = new ButtonType("Upload", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(uploadBtnType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField titleFld = new TextField();
        ComboBox<String> catCombo = new ComboBox<>(FXCollections.observableArrayList("GOVERNANCE", "COMPLIANCE", "FINANCE", "LEGAL", "GENERAL"));
        catCombo.getSelectionModel().selectFirst();
        TextField tagsFld = new TextField();
        TextField expiryFld = new TextField();
        expiryFld.setPromptText("DD-MM-YYYY");

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleFld, 1, 0);
        grid.add(new Label("Category:"), 0, 1);
        grid.add(catCombo, 1, 1);
        grid.add(new Label("Tags (comma separated):"), 0, 2);
        grid.add(tagsFld, 1, 2);
        grid.add(new Label("Expiry Date:"), 0, 3);
        grid.add(expiryFld, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == uploadBtnType) {
                DocumentEntity d = new DocumentEntity();
                d.setTitle(titleFld.getText().trim());
                d.setCategory(catCombo.getValue());
                d.setTags(tagsFld.getText().trim());
                d.setExpiryDate(expiryFld.getText().trim().isEmpty() ? null : expiryFld.getText().trim());
                return d;
            }
            return null;
        });

        Optional<DocumentEntity> result = dialog.showAndWait();
        result.ifPresent(d -> {
            if (!d.getTitle().isEmpty()) {
                // Simulate document upload: write a dummy text file to workspace 'documents/' folder
                File dir = new File("documents");
                if (!dir.exists()) {
                    dir.mkdir();
                }
                String safeName = d.getTitle().replaceAll("[^a-zA-Z0-9.-]", "_") + ".txt";
                File file = new File(dir, safeName);
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write("Cooperative Housing Society simulated document content for: " + d.getTitle());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                d.setFilePath(file.getAbsolutePath());
                d.setFileSize((int) file.length());
                d.setMimeType("text/plain");

                documentRepository.save(d);
                loadDocuments();
            }
        });
    }
}
