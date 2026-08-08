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

    @FXML private Button addMemberBtn;
    @FXML private Button editMemberBtn;
    @FXML private Button deleteMemberBtn;
    @FXML private Button bulkImportBtn;
    @FXML private Label totalMembersLabel;

    private final com.society.user.context.UserContext userContext;

    public MemberController(
            NavigationManager navigationManager,
            MemberService memberService,
            MemberContext memberContext,
            com.society.user.context.UserContext userContext) {

        super(navigationManager);
        this.memberService = memberService;
        this.memberContext = memberContext;
        this.userContext = userContext;
    }

    @FXML
    public void initialize() {
        configureTable();
        loadMembers();
        memberTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        applyRolePermissions();
    }

    private void applyRolePermissions() {
        boolean canEdit = userContext.canEdit("MEMBERS");
        if (addMemberBtn != null) { addMemberBtn.setVisible(canEdit); addMemberBtn.setManaged(canEdit); }
        if (editMemberBtn != null) { editMemberBtn.setVisible(canEdit); editMemberBtn.setManaged(canEdit); }
        if (deleteMemberBtn != null) { deleteMemberBtn.setVisible(canEdit); deleteMemberBtn.setManaged(canEdit); }
        if (bulkImportBtn != null) { bulkImportBtn.setVisible(canEdit); bulkImportBtn.setManaged(canEdit); }
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

    @FXML
    private void viewMemberDetails() {
        MemberDto selected = memberTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInformation("Please select a member to view details.");
            return;
        }
        memberContext.setSelectedMember(selected);
        navigationManager.navigate(View.MEMBER_DETAILS);
    }

    @FXML
    private void memberDoubleClicked(javafx.scene.input.MouseEvent event) {
        if (event.getClickCount() == 2) {
            viewMemberDetails();
        }
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

    @FXML
    private void downloadTemplate() {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Save Member CSV Template");
        fileChooser.setInitialFileName("member_import_template.csv");
        fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        java.io.File file = fileChooser.showSaveDialog(memberTable.getScene().getWindow());

        if (file != null) {
            String template = "Member Number,Membership Number,First Name,Middle Name,Last Name,Gender,DOB,Mobile Number,Email,Occupation,Member Type,Admission Date,Resolution Number,Resolution Date,Permanent Address,Correspondence Address\n" +
                    "M00101,MS00101,Rahul,Kumar,Sharma,MALE,1985-05-20,9876543210,rahul@example.com,Software Engineer,PRIMARY_MEMBER,2024-01-15,RES-101,2024-01-10,Block A Flat 101,Block A Flat 101\n";
            try {
                java.nio.file.Files.writeString(file.toPath(), template);
                showInformation("Member CSV Template downloaded successfully!");
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Failed to save template: " + ex.getMessage());
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void bulkImportCsv() {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Select Member CSV File to Import");
        fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        java.io.File file = fileChooser.showOpenDialog(memberTable.getScene().getWindow());

        if (file != null) {
            try {
                List<String> lines = java.nio.file.Files.readAllLines(file.toPath());
                if (lines.size() <= 1) {
                    showInformation("The selected CSV file is empty or has no data rows.");
                    return;
                }

                int successCount = 0;
                int failCount = 0;

                for (int i = 1; i < lines.size(); i++) {
                    String line = lines.get(i).trim();
                    if (line.isBlank()) continue;

                    String[] cols = line.split(",", -1);
                    if (cols.length >= 5) {
                        try {
                            String mNo = cols[0].trim();
                            String msNo = cols[1].trim();
                            String fn = cols[2].trim();
                            String mn = cols.length > 3 ? cols[3].trim() : "";
                            String ln = cols.length > 4 ? cols[4].trim() : "";
                            String gen = cols.length > 5 ? cols[5].trim() : "MALE";
                            String dob = cols.length > 6 ? cols[6].trim() : "";
                            String mob = cols.length > 7 ? cols[7].trim() : "";
                            String email = cols.length > 8 ? cols[8].trim() : "";
                            String occ = cols.length > 9 ? cols[9].trim() : "";
                            String mType = cols.length > 10 ? cols[10].trim() : "PRIMARY_MEMBER";
                            String admDate = cols.length > 11 ? cols[11].trim() : "";
                            String resNo = cols.length > 12 ? cols[12].trim() : "";
                            String resDate = cols.length > 13 ? cols[13].trim() : "";
                            String permAddr = cols.length > 14 ? cols[14].trim() : "";
                            String corrAddr = cols.length > 15 ? cols[15].trim() : "";

                            MemberDto dto = new MemberDto(
                                    null, mNo, msNo, fn, mn, ln, mob, email, gen, dob, occ,
                                    null, null, mType, admDate, resNo, resDate, permAddr, corrAddr,
                                    null, null, null, null, null,
                                    com.society.member.entity.MemberStatus.ACTIVE, true,
                                    java.util.Collections.emptyList(), java.util.Collections.emptyList()
                            );
                            memberService.register(dto);
                            successCount++;
                        } catch (Exception ex) {
                            failCount++;
                        }
                    }
                }

                showInformation("Bulk Import Complete!\nSuccessfully Imported: " + successCount + " members.\nFailed/Skipped: " + failCount);
                refresh();
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Failed to process CSV file: " + ex.getMessage());
                alert.showAndWait();
            }
        }
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