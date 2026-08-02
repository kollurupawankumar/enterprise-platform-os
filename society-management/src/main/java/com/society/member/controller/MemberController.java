package com.society.member.controller;

import com.society.common.controller.BaseController;
import com.society.common.navigation.NavigationManager;
import com.society.common.navigation.View;
import com.society.member.dto.MemberDto;
import com.society.member.service.MemberService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MemberController extends BaseController {

    private final MemberService memberService;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private Button newButton;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button refreshButton;

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
            MemberService memberService) {

        super(navigationManager);
        this.memberService = memberService;
    }

    @FXML
    public void initialize() {

        configureTable();

        refresh();

    }

    private void configureTable() {

        memberNumberColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().memberNumber()));

        membershipNumberColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().membershipNumber()));

        memberNameColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        cell.getValue().firstName() + " "
                                + (cell.getValue().lastName() == null
                                ? ""
                                : cell.getValue().lastName())));

        mobileColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().mobileNumber()));

        statusColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().status().name()));

    }

    public void refresh() {

        List<MemberDto> members = memberService.findAll();

        memberTable.setItems(FXCollections.observableArrayList(members));

        totalMembersLabel.setText("Total Members : " + members.size());

    }

    @FXML
    private void searchMembers() {

        // Will be implemented in Part 5

    }

    @FXML
    private void addMember() {

        navigationManager.navigate(
                View.MEMBER_REGISTRATION);

    }

    @FXML
    private void editMember() {

        // Part 4

    }

    @FXML
    private void deleteMember() {

        // Part 5

    }

}