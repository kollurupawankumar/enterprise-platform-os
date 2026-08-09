package com.society.finance.controller;

import com.society.common.controller.BaseController;
import com.society.common.navigation.NavigationManager;
import com.society.finance.entity.BudgetEntity;
import com.society.finance.entity.ExpenseEntity;
import com.society.finance.entity.AuditObservationEntity;
import com.society.finance.repository.BudgetRepository;
import com.society.finance.repository.ExpenseRepository;
import com.society.finance.repository.AuditObservationRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class BudgetAuditController extends BaseController {

    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;
    private final AuditObservationRepository auditObservationRepository;

    // Tab 1: Budgets
    @FXML
    private TableView<BudgetEntity> budgetTable;

    @FXML
    private TableColumn<BudgetEntity, String> budgetYearCol;

    @FXML
    private TableColumn<BudgetEntity, String> budgetCatCol;

    @FXML
    private TableColumn<BudgetEntity, String> budgetAllocatedCol;

    @FXML
    private TableColumn<BudgetEntity, String> budgetSpentCol;

    @FXML
    private TableColumn<BudgetEntity, String> budgetRemainingCol;

    private final ObservableList<BudgetEntity> budgets = FXCollections.observableArrayList();

    // Tab 2: Audits
    @FXML
    private TableView<AuditObservationEntity> auditTable;

    @FXML
    private TableColumn<AuditObservationEntity, String> auditDateCol;

    @FXML
    private TableColumn<AuditObservationEntity, String> auditAuditorCol;

    @FXML
    private TableColumn<AuditObservationEntity, String> auditObservationCol;

    @FXML
    private TableColumn<AuditObservationEntity, String> auditResponseCol;

    @FXML
    private TableColumn<AuditObservationEntity, String> auditStatusCol;

    private final ObservableList<AuditObservationEntity> observations = FXCollections.observableArrayList();

    public BudgetAuditController(
            NavigationManager navigationManager,
            BudgetRepository budgetRepository,
            ExpenseRepository expenseRepository,
            AuditObservationRepository auditObservationRepository) {
        super(navigationManager);
        this.budgetRepository = budgetRepository;
        this.expenseRepository = expenseRepository;
        this.auditObservationRepository = auditObservationRepository;
    }

    @FXML
    public void initialize() {
        // Budget Columns mapping
        budgetYearCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getYear()));
        budgetCatCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategory()));
        budgetAllocatedCol.setCellValueFactory(c -> new SimpleStringProperty("₹ " + String.format("%.2f", c.getValue().getAllocatedAmount())));
        budgetSpentCol.setCellValueFactory(c -> new SimpleStringProperty("₹ " + String.format("%.2f", c.getValue().getActualSpent())));
        budgetRemainingCol.setCellValueFactory(c -> {
            double rem = c.getValue().getAllocatedAmount() - c.getValue().getActualSpent();
            return new SimpleStringProperty("₹ " + String.format("%.2f", rem));
        });

        // Audit Columns mapping
        auditDateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getObservationDate()));
        auditAuditorCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAuditorName()));
        auditObservationCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getObservationText()));
        auditResponseCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getQueryTracker() != null ? c.getValue().getQueryTracker() : "-"));
        auditStatusCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus()));

        setupAuditDoubleClickListener();

        loadBudgets();
        loadObservations();
    }

    private void loadBudgets() {
        List<BudgetEntity> list = budgetRepository.findAll();
        List<ExpenseEntity> expensesList = expenseRepository.findAll();

        // Calculate actual spent dynamically by category
        for (BudgetEntity b : list) {
            double spent = expensesList.stream()
                    .filter(e -> e.getCategory() != null && e.getCategory().equalsIgnoreCase(b.getCategory()))
                    .mapToDouble(ExpenseEntity::getAmount)
                    .sum();
            b.setActualSpent(spent);
        }

        budgets.setAll(list);
        budgetTable.setItems(budgets);
    }

    private void loadObservations() {
        observations.setAll(auditObservationRepository.findAll());
        auditTable.setItems(observations);
    }

    @FXML
    private void handleNewBudget() {
        Dialog<BudgetEntity> dialog = new Dialog<>();
        dialog.setTitle("New Budget Allocation");
        dialog.setHeaderText("Allocate budget for financial year:");

        ButtonType allocateBtn = new ButtonType("Allocate", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(allocateBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField yearFld = new TextField("2026-27");
        ComboBox<String> catCombo = new ComboBox<>(FXCollections.observableArrayList(
                "MAINTENANCE", "SECURITY", "UTILITIES", "REPAIRS", "SALARIES", "EXTRAORDINARY"
        ));
        catCombo.getSelectionModel().selectFirst();
        TextField amountFld = new TextField();

        grid.add(new Label("Financial Year:"), 0, 0);
        grid.add(yearFld, 1, 0);
        grid.add(new Label("Category:"), 0, 1);
        grid.add(catCombo, 1, 1);
        grid.add(new Label("Allocated Amount:"), 0, 2);
        grid.add(amountFld, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == allocateBtn) {
                BudgetEntity b = new BudgetEntity();
                b.setYear(yearFld.getText().trim());
                b.setCategory(catCombo.getValue());
                try {
                    b.setAllocatedAmount(Double.parseDouble(amountFld.getText().trim()));
                } catch (NumberFormatException e) {
                    b.setAllocatedAmount(0.0);
                }
                b.setActualSpent(0.0);
                return b;
            }
            return null;
        });

        Optional<BudgetEntity> result = dialog.showAndWait();
        result.ifPresent(b -> {
            if (!b.getYear().isEmpty() && b.getAllocatedAmount() > 0) {
                budgetRepository.save(b);
                loadBudgets();
            }
        });
    }

    @FXML
    private void handleNewObservation() {
        Dialog<AuditObservationEntity> dialog = new Dialog<>();
        dialog.setTitle("New Auditor Observation");
        dialog.setHeaderText("Log an observation query:");

        ButtonType logBtn = new ButtonType("Log Query", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(logBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField auditorFld = new TextField();
        TextArea obsArea = new TextArea();
        obsArea.setPrefRowCount(4);
        obsArea.setWrapText(true);

        grid.add(new Label("Auditor Name:"), 0, 0);
        grid.add(auditorFld, 1, 0);
        grid.add(new Label("Observation Query:"), 0, 1);
        grid.add(obsArea, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == logBtn) {
                AuditObservationEntity a = new AuditObservationEntity();
                a.setAuditorName(auditorFld.getText().trim());
                a.setObservationText(obsArea.getText().trim());
                a.setObservationDate(LocalDate.now().toString());
                a.setStatus("OPEN");
                return a;
            }
            return null;
        });

        Optional<AuditObservationEntity> result = dialog.showAndWait();
        result.ifPresent(a -> {
            if (!a.getAuditorName().isEmpty() && !a.getObservationText().isEmpty()) {
                auditObservationRepository.save(a);
                loadObservations();
            }
        });
    }

    private void setupAuditDoubleClickListener() {
        auditTable.setRowFactory(tv -> {
            TableRow<AuditObservationEntity> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    AuditObservationEntity obs = row.getItem();
                    handleUpdateObservationQuery(obs);
                }
            });
            return row;
        });
    }

    private void handleUpdateObservationQuery(AuditObservationEntity obs) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Resolve Audit Observation");
        dialog.setHeaderText("Respond or change status for observation query:");

        ButtonType saveBtn = new ButtonType("Save Changes", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextArea responseArea = new TextArea(obs.getQueryTracker() != null ? obs.getQueryTracker() : "");
        responseArea.setPrefRowCount(4);
        responseArea.setWrapText(true);
        ComboBox<String> statusCombo = new ComboBox<>(FXCollections.observableArrayList("OPEN", "RESOLVED", "CLOSED"));
        statusCombo.getSelectionModel().select(obs.getStatus());

        grid.add(new Label("Auditor Query:"), 0, 0);
        grid.add(new Label(obs.getObservationText()), 1, 0);
        grid.add(new Label("Resolution Response:"), 0, 1);
        grid.add(responseArea, 1, 1);
        grid.add(new Label("Status:"), 0, 2);
        grid.add(statusCombo, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                obs.setQueryTracker(responseArea.getText().trim());
                obs.setStatus(statusCombo.getValue());
                return "SAVE";
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(s -> {
            auditObservationRepository.save(obs);
            loadObservations();
        });
    }
}
