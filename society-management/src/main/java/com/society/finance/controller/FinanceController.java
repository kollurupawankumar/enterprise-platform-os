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

    @FXML private TableView<BankAccountEntity> bankTable;
    @FXML private TableColumn<BankAccountEntity, String> bankNameCol;
    @FXML private TableColumn<BankAccountEntity, String> accNumCol;
    @FXML private TableColumn<BankAccountEntity, String> ifscCol;
    @FXML private TableColumn<BankAccountEntity, String> branchCol;
    @FXML private TableColumn<BankAccountEntity, String> balanceCol;
    @FXML private Label totalBankLabel;

    @FXML private TableView<InvestmentEntity> investmentTable;
    @FXML private TableColumn<InvestmentEntity, String> fdRefCol;
    @FXML private TableColumn<InvestmentEntity, String> fdInstCol;
    @FXML private TableColumn<InvestmentEntity, String> fdPrincipalCol;
    @FXML private TableColumn<InvestmentEntity, String> fdRateCol;
    @FXML private TableColumn<InvestmentEntity, String> fdMaturityCol;
    @FXML private Label totalFdLabel;

    private final ObservableList<ExpenseEntity> expenses = FXCollections.observableArrayList();
    private final ObservableList<BankAccountEntity> bankAccounts = FXCollections.observableArrayList();
    private final ObservableList<InvestmentEntity> investments = FXCollections.observableArrayList();

    @FXML private Button recordExpenseBtn;
    @FXML private Button addBankAccountBtn;
    @FXML private Button recordInvestmentBtn;

    // Expense Details Side Pane
    @FXML private Label categoryVal;
    @FXML private Label modeVal;
    @FXML private Label refNoVal;
    @FXML private Label approvedByVal;
    @FXML private TextArea narrationArea;
    @FXML private Label receiptPathVal;

    private ExpenseEntity selectedExpense;

    private final com.society.user.context.UserContext userContext;

    public FinanceController(
            NavigationManager navigationManager,
            BankAccountRepository bankAccountRepository,
            ExpenseRepository expenseRepository,
            InvestmentRepository investmentRepository,
            com.society.user.context.UserContext userContext) {
        super(navigationManager);
        this.bankAccountRepository = bankAccountRepository;
        this.expenseRepository = expenseRepository;
        this.investmentRepository = investmentRepository;
        this.userContext = userContext;
    }

    @FXML
    public void initialize() {
        // Expenses Columns
        voucherCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getVoucherNumber()));
        expenseDateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getExpenseDate()));
        payeeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPayee()));
        amountCol.setCellValueFactory(c -> new SimpleStringProperty(com.society.common.util.CurrencyUtils.formatInr(c.getValue().getAmount())));

        expenseTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            showExpenseDetails(newVal);
        });

        // Bank Accounts Columns
        bankNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBankName()));
        accNumCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAccountNumber()));
        ifscCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getIfsc()));
        branchCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBranch() != null ? c.getValue().getBranch() : "-"));
        balanceCol.setCellValueFactory(c -> new SimpleStringProperty(com.society.common.util.CurrencyUtils.formatInr(c.getValue().getBalance())));

        // Fixed Deposits Columns
        fdRefCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getReferenceNumber()));
        fdInstCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getInstitution()));
        fdPrincipalCol.setCellValueFactory(c -> new SimpleStringProperty(com.society.common.util.CurrencyUtils.formatInr(c.getValue().getPrincipalAmount())));
        fdRateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getInterestRate() != null ? c.getValue().getInterestRate() + " %" : "-"));
        fdMaturityCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMaturityDate() != null ? c.getValue().getMaturityDate() : "-"));

        loadExpenses();
        loadBankAccounts();
        loadInvestments();
        applyRolePermissions();
    }

    private void applyRolePermissions() {
        boolean canEdit = userContext.canEdit("FINANCE");
        if (recordExpenseBtn != null) { recordExpenseBtn.setVisible(canEdit); recordExpenseBtn.setManaged(canEdit); }
        if (addBankAccountBtn != null) { addBankAccountBtn.setVisible(canEdit); addBankAccountBtn.setManaged(canEdit); }
        if (recordInvestmentBtn != null) { recordInvestmentBtn.setVisible(canEdit); recordInvestmentBtn.setManaged(canEdit); }
    }

    private void loadExpenses() {
        expenses.setAll(expenseRepository.findAll());
        expenseTable.setItems(expenses);
    }

    private void loadBankAccounts() {
        List<BankAccountEntity> list = bankAccountRepository.findAll();
        bankAccounts.setAll(list);
        bankTable.setItems(bankAccounts);

        double total = list.stream().mapToDouble(b -> b.getBalance() != null ? b.getBalance() : 0.0).sum();
        totalBankLabel.setText(com.society.common.util.CurrencyUtils.formatInr(total));
    }

    private void loadInvestments() {
        List<InvestmentEntity> list = investmentRepository.findAll();
        investments.setAll(list);
        investmentTable.setItems(investments);

        double total = list.stream().mapToDouble(i -> i.getPrincipalAmount() != null ? i.getPrincipalAmount() : 0.0).sum();
        totalFdLabel.setText(com.society.common.util.CurrencyUtils.formatInr(total));
    }

    private void showExpenseDetails(ExpenseEntity expense) {
        this.selectedExpense = expense;
        if (expense == null) {
            if (categoryLabel != null) categoryLabel.setText("-");
            if (categoryVal != null) categoryVal.setText("-");
            if (modeLabel != null) modeLabel.setText("-");
            if (modeVal != null) modeVal.setText("-");
            if (refNoVal != null) refNoVal.setText("-");
            if (approvedByVal != null) approvedByVal.setText("-");
            if (remarksLabel != null) remarksLabel.setText("-");
            if (narrationArea != null) narrationArea.setText("");
            if (receiptPathVal != null) receiptPathVal.setText("No receipt attached");
            return;
        }
        if (categoryLabel != null) categoryLabel.setText(expense.getCategory());
        if (categoryVal != null) categoryVal.setText(expense.getCategory());
        if (modeLabel != null) modeLabel.setText(expense.getPaymentMode());
        if (modeVal != null) modeVal.setText(expense.getPaymentMode());
        if (refNoVal != null) refNoVal.setText(expense.getVoucherNumber());
        if (approvedByVal != null) approvedByVal.setText("Treasurer / Secretary");
        if (remarksLabel != null) remarksLabel.setText(expense.getRemarks() != null ? expense.getRemarks() : "-");
        if (narrationArea != null) narrationArea.setText(expense.getRemarks() != null ? expense.getRemarks() : "No narration provided.");
        if (receiptPathVal != null) receiptPathVal.setText("Receipt attached / On record");
    }

    @FXML
    private void handleViewReceipt() {
        if (selectedExpense == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Selection Required");
            alert.setHeaderText(null);
            alert.setContentText("Please select an expense voucher from the table first.");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Expense Receipt & Voucher Proof");
        alert.setHeaderText("Voucher Number: " + selectedExpense.getVoucherNumber());
        alert.setContentText("Payee: " + selectedExpense.getPayee() + "\n"
                + "Amount: " + com.society.common.util.CurrencyUtils.formatInr(selectedExpense.getAmount()) + "\n"
                + "Category: " + selectedExpense.getCategory() + "\n"
                + "Payment Mode: " + selectedExpense.getPaymentMode() + "\n"
                + "Narration: " + (selectedExpense.getRemarks() != null ? selectedExpense.getRemarks() : "N/A"));
        alert.showAndWait();
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
    private void handleBankClicked() {
        BankAccountEntity b = bankTable.getSelectionModel().getSelectedItem();
        if (b == null) return;

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Bank Account Details");
        alert.setHeaderText(b.getBankName() + " - Account Details");
        alert.setContentText(
                "Bank Name: " + b.getBankName() + "\n" +
                "Account Number: " + b.getAccountNumber() + "\n" +
                "IFSC Code: " + b.getIfsc() + "\n" +
                "Branch Name: " + (b.getBranch() != null ? b.getBranch() : "N/A") + "\n" +
                "Current Balance: " + com.society.common.util.CurrencyUtils.formatInr(b.getBalance())
        );
        alert.showAndWait();
    }

    @FXML
    private void handleInvestmentClicked() {
        InvestmentEntity i = investmentTable.getSelectionModel().getSelectedItem();
        if (i == null) return;

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Fixed Deposit Details");
        alert.setHeaderText("FD Certificate - " + i.getReferenceNumber());
        alert.setContentText(
                "Financial Institution: " + i.getInstitution() + "\n" +
                "FD Receipt/Ref No: " + i.getReferenceNumber() + "\n" +
                "Investment Type: " + i.getInvestmentType() + "\n" +
                "Principal Amount: " + com.society.common.util.CurrencyUtils.formatInr(i.getPrincipalAmount()) + "\n" +
                "Interest Rate: " + (i.getInterestRate() != null ? i.getInterestRate() + " % p.a." : "N/A") + "\n" +
                "Start Date: " + i.getStartDate() + "\n" +
                "Maturity Date: " + (i.getMaturityDate() != null ? i.getMaturityDate() : "N/A") + "\n" +
                "Status: " + i.getStatus()
        );
        alert.showAndWait();
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
        instField.setPromptText("e.g. State Bank of India");
        TextField refField = new TextField();
        refField.setPromptText("FD Receipt No / Ref");
        TextField princField = new TextField();
        princField.setPromptText("Principal Amount (₹)");
        TextField rateField = new TextField();
        rateField.setPromptText("Interest Rate % p.a.");
        
        DatePicker startDatePicker = new DatePicker(java.time.LocalDate.now());
        DatePicker maturityDatePicker = new DatePicker(java.time.LocalDate.now().plusYears(1));

        grid.add(new Label("Institution:"), 0, 0);
        grid.add(instField, 1, 0);
        grid.add(new Label("Receipt Reference:"), 0, 1);
        grid.add(refField, 1, 1);
        grid.add(new Label("Principal Amount (₹):"), 0, 2);
        grid.add(princField, 1, 2);
        grid.add(new Label("Interest Rate (%):"), 0, 3);
        grid.add(rateField, 1, 3);
        grid.add(new Label("Start Date:"), 0, 4);
        grid.add(startDatePicker, 1, 4);
        grid.add(new Label("Maturity Date:"), 0, 5);
        grid.add(maturityDatePicker, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == recordBtnType) {
                InvestmentEntity i = new InvestmentEntity();
                i.setInstitution(instField.getText().trim());
                i.setReferenceNumber(refField.getText().trim());
                i.setStartDate(startDatePicker.getValue() != null ? startDatePicker.getValue().toString() : java.time.LocalDate.now().toString());
                i.setMaturityDate(maturityDatePicker.getValue() != null ? maturityDatePicker.getValue().toString() : null);
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
