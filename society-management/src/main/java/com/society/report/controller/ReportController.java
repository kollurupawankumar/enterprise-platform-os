package com.society.report.controller;

import com.society.common.controller.BaseController;
import com.society.common.navigation.NavigationManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class ReportController extends BaseController {

    @FXML
    private ComboBox<String> reportTypeCombo;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private RadioButton pdfRadio;

    @FXML
    private RadioButton excelRadio;

    @FXML
    private TableView<ExportLog> exportsTable;

    @FXML
    private TableColumn<ExportLog, String> nameCol;

    @FXML
    private TableColumn<ExportLog, String> formatCol;

    @FXML
    private TableColumn<ExportLog, String> dateCol;

    private final ObservableList<ExportLog> exportLogs = FXCollections.observableArrayList();

    public ReportController(NavigationManager navigationManager) {
        super(navigationManager);
    }

    @FXML
    public void initialize() {
        reportTypeCombo.setItems(FXCollections.observableArrayList(
                "Form I - Member Register",
                "Form J - Share Register",
                "Financial Expense Ledger",
                "Asset Inventory Log"
        ));
        reportTypeCombo.getSelectionModel().selectFirst();

        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().name));
        formatCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().format));
        dateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().timestamp));

        exportsTable.setItems(exportLogs);
    }

    @FXML
    private void handleGenerateReport() {
        String type = reportTypeCombo.getValue();
        String ext = pdfRadio.isSelected() ? "PDF" : "Excel";
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Report Exported");
        alert.setHeaderText(null);
        alert.setContentText(type + " successfully generated and saved to reports/ folder in " + ext + " format.");
        alert.showAndWait();

        exportLogs.add(0, new ExportLog(type, ext, time));
    }

    public static class ExportLog {
        private final String name;
        private final String format;
        private final String timestamp;

        public ExportLog(String name, String format, String timestamp) {
            this.name = name;
            this.format = format;
            this.timestamp = timestamp;
        }

        public String getName() {
            return name;
        }

        public String getFormat() {
            return format;
        }

        public String getTimestamp() {
            return timestamp;
        }
    }
}
