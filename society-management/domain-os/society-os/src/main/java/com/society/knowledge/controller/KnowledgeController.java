package com.society.knowledge.controller;

import com.society.common.controller.BaseController;
import com.society.common.navigation.NavigationManager;
import com.society.knowledge.entity.KnowledgeEntity;
import com.society.knowledge.repository.KnowledgeRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class KnowledgeController extends BaseController {

    private final KnowledgeRepository knowledgeRepository;

    @FXML
    private ComboBox<String> categoryFilterCombo;

    @FXML
    private TableView<KnowledgeEntity> notesTable;

    @FXML
    private TableColumn<KnowledgeEntity, String> titleCol;

    @FXML
    private TableColumn<KnowledgeEntity, String> categoryCol;

    @FXML
    private TableColumn<KnowledgeEntity, String> authorCol;

    @FXML
    private Label noteTitleLabel;

    @FXML
    private Label noteMetaLabel;

    @FXML
    private TextArea noteContentArea;

    private final ObservableList<KnowledgeEntity> notes = FXCollections.observableArrayList();

    public KnowledgeController(NavigationManager navigationManager, KnowledgeRepository knowledgeRepository) {
        super(navigationManager);
        this.knowledgeRepository = knowledgeRepository;
    }

    @FXML
    public void initialize() {
        categoryFilterCombo.setItems(FXCollections.observableArrayList(
                "ALL", "SOP", "FAQ", "DECISION", "LESSONS_LEARNED", "BEST_PRACTICES"
        ));
        categoryFilterCombo.getSelectionModel().selectFirst();

        titleCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitle()));
        categoryCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategory()));
        authorCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAuthor() != null ? c.getValue().getAuthor() : "-"));

        notesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            showNoteDetails(newVal);
        });

        loadNotes();
    }

    private void loadNotes() {
        notes.setAll(knowledgeRepository.findAll());
        notesTable.setItems(notes);
    }

    private void showNoteDetails(KnowledgeEntity note) {
        if (note == null) {
            noteTitleLabel.setText("Select an article");
            noteMetaLabel.setText("Author: N/A | Category: N/A");
            noteContentArea.clear();
        } else {
            noteTitleLabel.setText(note.getTitle());
            noteMetaLabel.setText("Author: " + (note.getAuthor() != null ? note.getAuthor() : "Anonymous") + " | Category: " + note.getCategory());
            noteContentArea.setText(note.getContent());
        }
    }

    @FXML
    private void handleFilter() {
        String cat = categoryFilterCombo.getValue();
        if (cat == null || cat.equals("ALL")) {
            notes.setAll(knowledgeRepository.findAll());
        } else {
            notes.setAll(knowledgeRepository.findByCategory(cat));
        }
    }

    @FXML
    private void handleNewNote() {
        Dialog<KnowledgeEntity> dialog = new Dialog<>();
        dialog.setTitle("Create Knowledge Note");
        dialog.setHeaderText("Add SOP, FAQ or committee decision details:");

        ButtonType saveBtnType = new ButtonType("Save Note", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtnType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField titleFld = new TextField();
        ComboBox<String> catCombo = new ComboBox<>(FXCollections.observableArrayList("SOP", "FAQ", "DECISION", "LESSONS_LEARNED", "BEST_PRACTICES"));
        catCombo.getSelectionModel().selectFirst();
        TextField authorFld = new TextField();
        TextArea contentArea = new TextArea();
        contentArea.setPrefRowCount(5);
        contentArea.setWrapText(true);

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleFld, 1, 0);
        grid.add(new Label("Category:"), 0, 1);
        grid.add(catCombo, 1, 1);
        grid.add(new Label("Author:"), 0, 2);
        grid.add(authorFld, 1, 2);
        grid.add(new Label("Content:"), 0, 3);
        grid.add(contentArea, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtnType) {
                KnowledgeEntity k = new KnowledgeEntity();
                k.setTitle(titleFld.getText().trim());
                k.setCategory(catCombo.getValue());
                k.setAuthor(authorFld.getText().trim());
                k.setContent(contentArea.getText().trim());
                return k;
            }
            return null;
        });

        Optional<KnowledgeEntity> result = dialog.showAndWait();
        result.ifPresent(k -> {
            if (!k.getTitle().isEmpty() && !k.getContent().isEmpty()) {
                knowledgeRepository.save(k);
                loadNotes();
            }
        });
    }
}
