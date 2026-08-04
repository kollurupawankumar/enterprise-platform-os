package com.society.member.controller;

import com.society.common.controller.BaseController;
import com.society.common.navigation.NavigationManager;
import com.society.common.navigation.View;
import com.society.member.context.MemberContext;
import com.society.member.dto.MemberDto;
import com.society.member.service.MemberService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MemberController extends BaseController {

    private final MemberService memberService;
    private final MemberContext memberContext;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<MemberDto> memberTable;

    @FXML
    private TableColumn<MemberDto, String> memberNumberColumn;

    @FXML
    private TableColumn<MemberDto, String> membershipNumberColumn;

    @FXML
    private TableColumn<MemberDto, String> memberNameColumn;

    @FXML
    private TableColumn<MemberDto, String> mobileColumn;

    @FXML
    private TableColumn<MemberDto, String> statusColumn;

    @FXML
    private Label totalMembersLabel;

    public MemberController(
            NavigationManager navigationManager,
            MemberService memberService,
            MemberContext memberContext) {

        super(navigationManager);
        this.memberService = memberService;
        this.memberContext = memberContext;
    }

    @FXML
    public void initialize() {

        configureTable();

        loadMembers();

        memberTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY);

    }

    /**
     * Configure all table columns.
     */
    private void configureTable() {

        memberNumberColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        cell.getValue().memberNumber()));

        membershipNumberColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        cell.getValue().membershipNumber()));

        memberNameColumn.setCellValueFactory(cell -> {

            String first = cell.getValue().firstName() == null
                    ? ""
                    : cell.getValue().firstName();

            String last = cell.getValue().lastName() == null
                    ? ""
                    : cell.getValue().lastName();

            return new SimpleStringProperty(
                    (first + " " + last).trim());

        });

        mobileColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        cell.getValue().mobileNumber()));

        statusColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        cell.getValue().status().name()));

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
                    } else if ("INACTIVE".equalsIgnoreCase(item)) {
                        getStyleClass().add("status-inactive");
                    } else {
                        getStyleClass().add("status-other");
                    }
                }
            }
        });

    }

    /**
     * Loads all members from database.
     */
    private void loadMembers() {

        List<MemberDto> members = memberService.findAll();

        memberTable.setItems(
                FXCollections.observableArrayList(members));

        totalMembersLabel.setText(
                "Total Members : " + members.size());

    }

    /**
     * Refresh member list.
     */
    @FXML
    public void refresh() {

        searchField.clear();

        loadMembers();

    }

    /**
     * Open member registration screen for a new member.
     */
    @FXML
    private void addMember() {

        memberContext.clearSelectedMember();
        navigationManager.navigate(View.MEMBER_REGISTRATION);

    }

    /**
     * Edit selected member.
     */
    @FXML
    private void editMember() {

        MemberDto selected =
                memberTable.getSelectionModel().getSelectedItem();

        if (selected == null) {

            showInformation(
                    "Please select a member to edit.");

            return;

        }

        memberContext.setSelectedMember(selected);

        navigationManager.navigate(
                View.MEMBER_REGISTRATION);

    }

    /**
     * Soft delete (Deactivate) member.
     */
    @FXML
    private void deleteMember() {

        MemberDto selected =
                memberTable.getSelectionModel().getSelectedItem();

        if (selected == null) {

            showInformation(
                    "Please select a member.");

            return;

        }

        Alert alert =
                new Alert(Alert.AlertType.CONFIRMATION);

        alert.setTitle("Deactivate Member");
        alert.setHeaderText(null);
        alert.setContentText(
                "Do you want to deactivate this member?");

        alert.showAndWait()
                .filter(button -> button == ButtonType.OK)
                .ifPresent(button -> {

                    memberService.deactivate(
                            selected.id());

                    refresh();

                });

    }

    /**
     * Search members.
     */
    @FXML
    private void searchMembers() {

        String keyword = searchField.getText();

        List<MemberDto> members =
                memberService.search(keyword);

        memberTable.setItems(
                FXCollections.observableArrayList(members));

        totalMembersLabel.setText(
                "Total Members : " + members.size());

    }

    /**
     * Called when Refresh button is clicked.
     */
    @FXML
    private void refreshMembers() {

        refresh();

    }

    /**
     * Double click support.
     */
    @FXML
    private void memberDoubleClicked() {

        if (memberTable.getSelectionModel().getSelectedItem()
                != null) {

            editMember();

        }

    }

    /**
     * Information dialog.
     */
    private void showInformation(String message) {

        Alert alert =
                new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();

    }

}