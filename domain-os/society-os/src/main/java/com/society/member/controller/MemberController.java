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

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class MemberController extends BaseController {

    private final MemberService memberService;
    private final MemberContext memberContext;

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
    private TextField searchField;

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

        if (memberNumberColumn != null) {
            memberNumberColumn.setCellValueFactory(data ->
                    new SimpleStringProperty(data.getValue().memberNumber()));
        }

        if (membershipNumberColumn != null) {
            membershipNumberColumn.setCellValueFactory(data ->
                    new SimpleStringProperty(data.getValue().membershipNumber() != null ? data.getValue().membershipNumber() : ""));
        }

        if (memberNameColumn != null) {
            memberNameColumn.setCellValueFactory(data -> {
                String fullName = data.getValue().firstName() + " " + (data.getValue().lastName() != null ? data.getValue().lastName() : "");
                return new SimpleStringProperty(fullName.trim());
            });
        }

        if (mobileColumn != null) {
            mobileColumn.setCellValueFactory(data ->
                    new SimpleStringProperty(data.getValue().mobileNumber()));
        }

        if (statusColumn != null) {
            statusColumn.setCellValueFactory(data ->
                    new SimpleStringProperty(data.getValue().status() != null ? data.getValue().status().name() : "ACTIVE"));
        }

        loadMembers();

    }

    private void loadMembers() {

        List<MemberDto> members = memberService.findAll();

        memberTable.setItems(
                FXCollections.observableArrayList(members));

        totalMembersLabel.setText(
                "Total Members : " + members.size());

    }

    @FXML
    public void refresh() {

        searchField.clear();

        loadMembers();

    }

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
        File file = fileChooser.showSaveDialog(memberTable.getScene().getWindow());

        if (file != null) {
            String template = "First Name,Middle Name,Last Name,Gender,DOB,Mobile Number,Email,Occupation,Member Type,Admission Date,Resolution Number,Resolution Date,Permanent Address,Correspondence Address\n" +
                    "Rahul,Kumar,Sharma,MALE,20/05/1985,9876543210,rahul@example.com,Software Engineer,PRIMARY_MEMBER,15/01/2024,RES-101,10/01/2024,Block A Flat 101,Block A Flat 101\n";
            try {
                Files.writeString(file.toPath(), template);
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
        File file = fileChooser.showOpenDialog(memberTable.getScene().getWindow());

        if (file != null) {
            try {
                List<String> lines = Files.readAllLines(file.toPath());
                if (lines.size() <= 1) {
                    showInformation("The selected CSV file is empty or has no data rows.");
                    return;
                }

                String headerLine = lines.get(0);
                String[] headers = parseCsvLine(headerLine);
                Map<String, Integer> colMap = new HashMap<>();
                for (int i = 0; i < headers.length; i++) {
                    String h = headers[i].trim().toLowerCase().replaceAll("[^a-z0-9]", "");
                    colMap.put(h, i);
                }

                int successCount = 0;
                int failCount = 0;
                List<String> errorMessages = new ArrayList<>();

                for (int i = 1; i < lines.size(); i++) {
                    String line = lines.get(i).trim();
                    if (line.isBlank()) continue;

                    String[] cols = parseCsvLine(line);
                    try {
                        String fn = getColValue(cols, colMap, "firstname", "fn");
                        String mn = getColValue(cols, colMap, "middlename", "mn");
                        String ln = getColValue(cols, colMap, "lastname", "ln");

                        if (fn.isEmpty()) {
                            failCount++;
                            errorMessages.add("Row " + (i + 1) + ": First Name is mandatory.");
                            continue;
                        }

                        String genRaw = getColValue(cols, colMap, "gender");
                        String gen = genRaw.equalsIgnoreCase("FEMALE") ? "FEMALE" : "MALE";

                        String dobRaw = getColValue(cols, colMap, "dob", "dateofbirth");
                        String dobParsed = parseFlexibleDate(dobRaw);

                        String mob = getColValue(cols, colMap, "mobilenumber", "mobile", "phone");
                        if (mob.isEmpty() || !mob.replaceAll("\\s+", "").matches("^[0-9]{10}$")) {
                            mob = "9" + String.format("%09d", (long)(Math.random() * 1_000_000_000L));
                        }

                        String email = getColValue(cols, colMap, "email", "emailaddress");
                        String occ = getColValue(cols, colMap, "occupation");
                        String mTypeRaw = getColValue(cols, colMap, "membertype", "type");
                        String mType = mTypeRaw.isBlank() ? "PRIMARY_MEMBER" : mTypeRaw.toUpperCase();

                        String admDateRaw = getColValue(cols, colMap, "admissiondate", "admdate");
                        String admDateParsed = parseFlexibleDate(admDateRaw);

                        String resNo = getColValue(cols, colMap, "resolutionnumber", "resno");
                        String resDateRaw = getColValue(cols, colMap, "resolutiondate", "resdate");
                        String resDateParsed = parseFlexibleDate(resDateRaw);

                        String permAddr = getColValue(cols, colMap, "permanentaddress", "address");
                        String corrAddr = getColValue(cols, colMap, "correspondenceaddress", "corraddress");
                        if (corrAddr.isEmpty()) corrAddr = permAddr;

                        String mNo = getColValue(cols, colMap, "membernumber", "mno");
                        String msNo = getColValue(cols, colMap, "membershipnumber", "msno");

                        MemberDto dto = new MemberDto(
                                null,
                                mNo.isEmpty() ? null : mNo,
                                msNo.isEmpty() ? null : msNo,
                                fn, mn, ln, mob, email, gen, dobParsed, occ,
                                null, null, mType, admDateParsed, resNo, resDateParsed, permAddr, corrAddr,
                                null, null, null, null, null,
                                com.society.member.entity.MemberStatus.ACTIVE, true,
                                Collections.emptyList(), Collections.emptyList()
                        );

                        memberService.register(dto);
                        successCount++;
                    } catch (Exception ex) {
                        failCount++;
                        errorMessages.add("Row " + (i + 1) + ": " + ex.getMessage());
                    }
                }

                StringBuilder summary = new StringBuilder();
                summary.append("Bulk Import Summary:\n");
                summary.append("✅ Successfully Imported: ").append(successCount).append(" members\n");
                summary.append("❌ Failed / Skipped: ").append(failCount).append(" rows\n");

                if (!errorMessages.isEmpty()) {
                    summary.append("\nErrors:\n");
                    int maxDisplay = Math.min(errorMessages.size(), 5);
                    for (int k = 0; k < maxDisplay; k++) {
                        summary.append("- ").append(errorMessages.get(k)).append("\n");
                    }
                    if (errorMessages.size() > 5) {
                        summary.append("... and ").append(errorMessages.size() - 5).append(" more errors.");
                    }
                }

                showInformation(summary.toString());
                refresh();
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Failed to process CSV file: " + ex.getMessage());
                alert.showAndWait();
            }
        }
    }

    private String[] parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder sb = new StringBuilder();
        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        result.add(sb.toString().trim());
        return result.toArray(new String[0]);
    }

    private String getColValue(String[] cols, Map<String, Integer> colMap, String... possibleKeys) {
        for (String key : possibleKeys) {
            Integer idx = colMap.get(key);
            if (idx != null && idx < cols.length) {
                return cols[idx].trim();
            }
        }
        return "";
    }

    private String parseFlexibleDate(String rawDate) {
        if (rawDate == null || rawDate.isBlank()) {
            return LocalDate.now().toString();
        }

        String cleaned = rawDate.trim();
        List<DateTimeFormatter> formatters = List.of(
                DateTimeFormatter.ofPattern("d/M/yyyy"),
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("d/M/yy"),
                DateTimeFormatter.ofPattern("dd/MM/yy"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("d-M-yyyy"),
                DateTimeFormatter.ofPattern("dd-MM-yyyy"),
                DateTimeFormatter.ofPattern("d-M-yy"),
                DateTimeFormatter.ofPattern("dd-MM-yy")
        );

        for (DateTimeFormatter fmt : formatters) {
            try {
                LocalDate date = LocalDate.parse(cleaned, fmt);
                return date.toString();
            } catch (Exception ignored) {
            }
        }

        return LocalDate.now().toString();
    }

    @FXML
    private void memberDoubleClicked() {

        if (memberTable.getSelectionModel().getSelectedItem()
                != null) {

            editMember();

        }

    }

    private void showInformation(String message) {

        Alert alert =
                new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();

    }

}