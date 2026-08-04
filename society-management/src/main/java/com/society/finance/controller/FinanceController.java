package com.society.finance.controller;

import com.society.common.controller.BaseController;
import com.society.common.navigation.NavigationManager;
import com.society.finance.entity.BankAccountEntity;
import com.society.finance.entity.ExpenseEntity;
import com.society.finance.entity.InvestmentEntity;
import com.society.finance.repository.BankAccountRepository;
import com.society.finance.repository.ExpenseRepository;
import com.society.finance.repository.InvestmentRepository;
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

@Component
public class FinanceController extends BaseController {

    private final BankAccountRepository bankAccountRepository;
    private final ExpenseRepository expenseRepository;
    private final InvestmentRepository investmentRepository;

    @FXML
    private TableView<ExpenseEntity> expenseTable;

    @FXML
    private TableColumn<ExpenseEntity, String> voucherCol;

    @FXML
    private TableColumn<ExpenseEntity, String> expenseDateCol;

    @FXML
    private TableColumn<ExpenseEntity, String> payeeCol;

    @FXML
    private TableColumn<ExpenseEntity, String> amountCol;

    @FXML
    private Label categoryLabel;

    @FXML
    private Label modeLabel;

    @FXML
    private Label remarksLabel;

    @FXML
    private TableView<BankAccountEntity> bankTable;

    @FXML
    private TableColumn<BankAccountEntity, String> bankNameCol;

    @FXML
    private TableColumn<BankAccountEntity, String> accNumCol;

    @FXML
    private TableColumn<BankAccountEntity, String> balanceCol;

    @FXML
    private TableView<InvestmentEntity> investmentTable;

    @FXML
    private TableColumn<InvestmentEntity, String> fdInstCol;

    @FXML
    private TableColumn<InvestmentEntity, String> fdPrincipalCol;

    @FXML
    private TableColumn<InvestmentEntity, String> fdMaturityCol;

    private final ObservableList<ExpenseEntity> expenses = FXCollections.observableArrayList();
    private final ObservableList<BankAccountEntity> bankAccounts = FXCollections.observableArrayList();
    private final ObservableList<InvestmentEntity> investments = FXCollections.observableArrayList();

    public FinanceController(
            NavigationManager navigationManager,
            BankAccountRepository bankAccountRepository,
            ExpenseRepository expenseRepository,
            InvestmentRepository investmentRepository) {
        super(navigationManager);
        this.bankAccountRepository = bankAccountRepository;
        this.expenseRepository = expenseRepository;
        this.investmentRepository = investmentRepository;
    }

    @FXML
    public void initialize() {
        // Expenses Columns
        voucherCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getVoucherNumber()));
        expenseDateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getExpenseDate()));
        payeeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPayee()));
        amountCol.setCellValueFactory(c -> new SimpleStringProperty("$" + c.getValue().getAmount()));

        expenseTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            showExpenseDetails(newVal);
        });

        // Bank Accounts Columns
        bankNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBankName()));
        accNumCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAccountNumber()));
        balanceCol.setCellValueFactory(c -> new SimpleStringProperty("$" + c.getValue().getBalance()));

        // Fixed Deposits Columns
        fdInstCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getInstitution()));
        fdPrincipalCol.setCellValueFactory(c -> new SimpleStringProperty("$" + c.getValue().getPrincipalAmount()));
        fdMaturityCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMaturityDate() != null ? c.getValue().getMaturityDate() : "-"));

        loadExpenses();
        loadBankAccounts();
        loadInvestments();
    }

    private void loadExpenses() {
        expenses.setAll(expenseRepository.findAll());
        expenseTable.setItems(expenses);
    }

    private void loadBankAccounts() {
        bankAccounts.setAll(bankAccountRepository.findAll());
        bankTable.setItems(bankAccounts);
    }

    private void loadInvestments() {
        investments.setAll(investmentRepository.findAll());
        investmentTable.setItems(investments);
    }

    private void showExpenseDetails(ExpenseEntity expense) {
        if (expense == null) {
            categoryLabel.setText("-");
            modeLabel.setText("-");
            remarksLabel.setText("-");
            return;
        }
        categoryLabel.setText(expense.getCategory());
        modeLabel.setText(expense.getPaymentMode());
        remarksLabel.setText(expense.getRemarks() != null ? expense.getRemarks() : "-");
    }

    @FXML
    private void handleRecordExpense() {
        List<BankAccountEntity> accounts = bankAccountRepository.findAll();
        if (accounts.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Account Required");
            alert.setHeaderText(null);
            alert.setContentText("Please add a bank account first before recording expenses.");
            alert.showAndWait();
            return;
        }

        Dialog<ExpenseEntity> dialog = new Dialog<>();
        dialog.setTitle("Record Expense");
        dialog.setHeaderText("Create digital payment voucher details:");

        ButtonType recordBtnType = new ButtonType("Record", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(recordBtnType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField vNoField = new TextField();
        vNoField.setPromptText("e.g. VOU-101");
        TextField dateField = new TextField();
        dateField.setPromptText("YYYY-MM-DD");
        TextField payeeField = new TextField();
        TextField amountField = new TextField();
        ComboBox<String> catCombo = new ComboBox<>(FXCollections.observableArrayList("MAINTENANCE", "REPAIRS", "SALARIES", "UTILITIES"));
        catCombo.getSelectionModel().selectFirst();
        ComboBox<String> modeCombo = new ComboBox<>(FXCollections.observableArrayList("BANK_TRANSFER", "CHEQUE", "CASH"));
        modeCombo.getSelectionModel().selectFirst();
        ComboBox<BankAccountEntity> bankCombo = new ComboBox<>(FXCollections.observableArrayList(accounts));
        bankCombo.getSelectionModel().selectFirst();
        TextArea remarksArea = new TextArea();
        remarksArea.setPrefRowCount(3);

        grid.add(new Label("Voucher Number:"), 0, 0);
        grid.add(vNoField, 1, 0);
        grid.add(new Label("Date:"), 0, 1);
        grid.add(dateField, 1, 1);
        grid.add(new Label("Payee:"), 0, 2);
        grid.add(payeeField, 1, 2);
        grid.add(new Label("Amount:"), 0, 3);
        grid.add(amountField, 1, 3);
        grid.add(new Label("Category:"), 0, 4);
        grid.add(catCombo, 1, 4);
        grid.add(new Label("Mode:"), 0, 5);
        grid.add(modeCombo, 1, 5);
        grid.add(new Label("Debit Account:"), 0, 6);
        grid.add(bankCombo, 1, 6);
        grid.add(new Label("Remarks:"), 0, 7);
        grid.add(remarksArea, 1, 7);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == recordBtnType) {
                ExpenseEntity e = new ExpenseEntity();
                e.setVoucherNumber(vNoField.getText().trim());
                e.setExpenseDate(dateField.getText().trim());
                e.setPayee(payeeField.getText().trim());
                e.setCategory(catCombo.getValue());
                e.setPaymentMode(modeCombo.getValue());
                e.setBankAccount(bankCombo.getValue());
                e.setRemarks(remarksArea.getText().trim());
                try {
                    e.setAmount(Double.parseDouble(amountField.getText().trim()));
                } catch (NumberFormatException ignored) {}
                return e;
            }
            return null;
        });

        Optional<ExpenseEntity> result = dialog.showAndWait();
        result.ifPresent(e -> {
            if (!e.getVoucherNumber().isEmpty() && e.getAmount() != null) {
                // Deduct balance
                BankAccountEntity acc = e.getBankAccount();
                acc.setBalance(acc.getBalance() - e.getAmount());
                bankAccountRepository.save(acc);

                expenseRepository.save(e);
                loadExpenses();
                loadBankAccounts();
            }
        });
    }

    @FXML
    private void handleAddBankAccount() {
        Dialog<BankAccountEntity> dialog = new Dialog<>();
        dialog.setTitle("Add Bank Account");
        dialog.setHeaderText("Specify bank account registers details:");

        ButtonType addBtnType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addBtnType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField bankField = new TextField();
        TextField accNumField = new TextField();
        TextField ifscField = new TextField();
        TextField balField = new TextField();
        balField.setPromptText("e.g. 5000.00");

        grid.add(new Label("Bank Name:"), 0, 0);
        grid.add(bankField, 1, 0);
        grid.add(new Label("Account Number:"), 0, 1);
        grid.add(accNumField, 1, 1);
        grid.add(new Label("IFSC Code:"), 0, 2);
        grid.add(ifscField, 1, 2);
        grid.add(new Label("Opening Balance:"), 0, 3);
        grid.add(balField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addBtnType) {
                BankAccountEntity a = new BankAccountEntity();
                a.setBankName(bankField.getText().trim());
                a.setAccountNumber(accNumField.getText().trim());
                a.setIfsc(ifscField.getText().trim());
                try {
                    a.setBalance(Double.parseDouble(balField.getText().trim()));
                } catch (NumberFormatException ignored) {
                    a.setBalance(0.0);
                }
                return a;
            }
            return null;
        });

        Optional<BankAccountEntity> result = dialog.showAndWait();
        result.ifPresent(a -> {
            if (!a.getBankName().isEmpty() && !a.getAccountNumber().isEmpty()) {
                bankAccountRepository.save(a);
                loadBankAccounts();
            }
        });
    }

    @FXML
    private void handleRecordInvestment() {
        Dialog<InvestmentEntity> dialog = new Dialog<>();
        dialog.setTitle("Record FD Investment");
        dialog.setHeaderText("Create Fixed Deposit (FD) entry:");

        ButtonType recordBtnType = new ButtonType("Record", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(recordBtnType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField instField = new TextField();
        instField.setPromptText("e.g. SBI Bank");
        TextField refField = new TextField();
        refField.setPromptText("FD Receipt No");
        TextField princField = new TextField();
        TextField rateField = new TextField();
        rateField.setPromptText("Interest Rate %");
        TextField startField = new TextField();
        startField.setPromptText("YYYY-MM-DD");
        TextField endField = new TextField();
        endField.setPromptText("Maturity YYYY-MM-DD");

        grid.add(new Label("Institution:"), 0, 0);
        grid.add(instField, 1, 0);
        grid.add(new Label("Receipt Reference:"), 0, 1);
        grid.add(refField, 1, 1);
        grid.add(new Label("Principal Amount:"), 0, 2);
        grid.add(princField, 1, 2);
        grid.add(new Label("Interest Rate:"), 0, 3);
        grid.add(rateField, 1, 3);
        grid.add(new Label("Start Date:"), 0, 4);
        grid.add(startField, 1, 4);
        grid.add(new Label("Maturity Date:"), 0, 5);
        grid.add(endField, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == recordBtnType) {
                InvestmentEntity i = new InvestmentEntity();
                i.setInstitution(instField.getText().trim());
                i.setReferenceNumber(refField.getText().trim());
                i.setStartDate(startField.getText().trim());
                i.setMaturityDate(endField.getText().trim());
                i.setInvestmentType("FD");
                i.setStatus("ACTIVE");
                try {
                    i.setPrincipalAmount(Double.parseDouble(princField.getText().trim()));
                } catch (NumberFormatException ignored) {}
                try {
                    i.setInterestRate(Double.parseDouble(rateField.getText().trim()));
                } catch (NumberFormatException ignored) {}
                return i;
            }
            return null;
        });

        Optional<InvestmentEntity> result = dialog.showAndWait();
        result.ifPresent(i -> {
            if (!i.getInstitution().isEmpty() && i.getPrincipalAmount() != null) {
                investmentRepository.save(i);
                loadInvestments();
            }
        });
    }
}
