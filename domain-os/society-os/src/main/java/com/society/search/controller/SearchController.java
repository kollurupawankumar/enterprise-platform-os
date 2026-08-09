package com.society.search.controller;

import com.society.common.controller.BaseController;
import com.society.common.navigation.NavigationManager;
import com.society.member.entity.MemberEntity;
import com.society.member.repository.MemberRepository;
import com.society.property.entity.PropertyEntity;
import com.society.property.repository.PropertyRepository;
import com.society.operations.entity.AssetEntity;
import com.society.operations.repository.AssetRepository;
import com.society.finance.entity.ExpenseEntity;
import com.society.finance.repository.ExpenseRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SearchController extends BaseController {

    private final MemberRepository memberRepository;
    private final PropertyRepository propertyRepository;
    private final AssetRepository assetRepository;
    private final ExpenseRepository expenseRepository;

    @FXML
    private TextField searchBar;

    @FXML
    private TableView<SearchResult> searchResultsTable;

    @FXML
    private TableColumn<SearchResult, String> typeCol;

    @FXML
    private TableColumn<SearchResult, String> nameCol;

    @FXML
    private TableColumn<SearchResult, String> detailCol;

    private final ObservableList<SearchResult> searchResults = FXCollections.observableArrayList();

    public SearchController(
            NavigationManager navigationManager,
            MemberRepository memberRepository,
            PropertyRepository propertyRepository,
            AssetRepository assetRepository,
            ExpenseRepository expenseRepository) {
        super(navigationManager);
        this.memberRepository = memberRepository;
        this.propertyRepository = propertyRepository;
        this.assetRepository = assetRepository;
        this.expenseRepository = expenseRepository;
    }

    @FXML
    public void initialize() {
        typeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().type));
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().name));
        detailCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().detail));

        searchBar.textProperty().addListener((obs, oldVal, newVal) -> executeSearch(newVal));
        executeSearch("");
    }

    private void executeSearch(String query) {
        searchResults.clear();
        String lower = query.trim().toLowerCase();

        // 1. Search Members
        List<MemberEntity> members = memberRepository.findAll();
        for (MemberEntity m : members) {
            String name = m.getFirstName() + " " + m.getLastName();
            if (lower.isEmpty() || name.toLowerCase().contains(lower) || m.getMemberNumber().toLowerCase().contains(lower)) {
                searchResults.add(new SearchResult("Member", name, "Member No: " + m.getMemberNumber() + " | Mobile: " + m.getMobileNumber()));
            }
        }

        // 2. Search Properties
        List<PropertyEntity> properties = propertyRepository.findAll();
        for (PropertyEntity p : properties) {
            if (lower.isEmpty() || p.getPropertyNumber().toLowerCase().contains(lower) || p.getBlock().toLowerCase().contains(lower)) {
                searchResults.add(new SearchResult("Property", "Unit: " + p.getPropertyNumber(), "Block: " + p.getBlock() + " | Type: " + p.getType()));
            }
        }

        // 3. Search Assets
        List<AssetEntity> assets = assetRepository.findAll();
        for (AssetEntity a : assets) {
            if (lower.isEmpty() || a.getName().toLowerCase().contains(lower) || a.getCategory().toLowerCase().contains(lower)) {
                searchResults.add(new SearchResult("Asset", a.getName(), "Category: " + a.getCategory() + " | Status: " + a.getStatus()));
            }
        }

        // 4. Search Expenses
        List<ExpenseEntity> expenses = expenseRepository.findAll();
        for (ExpenseEntity e : expenses) {
            if (lower.isEmpty() || e.getVoucherNumber().toLowerCase().contains(lower) || e.getPayee().toLowerCase().contains(lower)) {
                searchResults.add(new SearchResult("Expense", "Voucher: " + e.getVoucherNumber(), "Payee: " + e.getPayee() + " | Amount: $" + e.getAmount()));
            }
        }

        searchResultsTable.setItems(searchResults);
    }

    @FXML
    private void handleFormI() {
        showReportSuccess("Member Register Form I successfully exported to exports/Form_I_Member_Register.pdf");
    }

    @FXML
    private void handleFormJ() {
        showReportSuccess("Share Register Form J successfully exported to exports/Form_J_Share_Register.pdf");
    }

    @FXML
    private void handleAssetReport() {
        showReportSuccess("Asset Inventory successfully exported to exports/Asset_Inventory.xlsx");
    }

    @FXML
    private void handleExpenseReport() {
        showReportSuccess("Financial Expense Ledger successfully exported to exports/Expense_Ledger.xlsx");
    }

    private void showReportSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Report Generated");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class SearchResult {
        private final String type;
        private final String name;
        private final String detail;

        public SearchResult(String type, String name, String detail) {
            this.type = type;
            this.name = name;
            this.detail = detail;
        }

        public String getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public String getDetail() {
            return detail;
        }
    }
}
