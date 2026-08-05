package com.society.governance.controller;

import com.society.common.controller.BaseController;
import com.society.common.navigation.NavigationManager;
import com.society.governance.entity.ManagingCommitteeEntity;
import com.society.governance.entity.MeetingEntity;
import com.society.governance.entity.ResolutionEntity;
import com.society.governance.service.GovernanceService;
import com.society.member.entity.MemberEntity;
import com.society.member.repository.MemberRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.springframework.stereotype.Component;

import javafx.print.PrinterJob;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class GovernanceController extends BaseController {

    private final GovernanceService governanceService;
    private final MemberRepository memberRepository;

    @FXML private TableView<MeetingEntity> meetingTable;
    @FXML private TableColumn<MeetingEntity, String> meetTitleCol;
    @FXML private TableColumn<MeetingEntity, String> meetTypeCol;
    @FXML private TableColumn<MeetingEntity, String> meetDateCol;
    @FXML private TableColumn<MeetingEntity, String> meetStatusCol;

    @FXML private TextArea agendaArea;
    @FXML private TextArea minutesArea;
    @FXML private Button completeMeetingBtn;

    @FXML private TableView<ResolutionEntity> resolutionTable;
    @FXML private TableColumn<ResolutionEntity, String> resNumCol;
    @FXML private TableColumn<ResolutionEntity, String> resSubjectCol;
    @FXML private TableColumn<ResolutionEntity, String> resMeetingCol;
    @FXML private TableColumn<ResolutionEntity, String> resProposerCol;
    @FXML private TableColumn<ResolutionEntity, String> resStatusCol;

    @FXML private TableView<ManagingCommitteeEntity> mcTable;
    @FXML private TableColumn<ManagingCommitteeEntity, String> mcMemberCol;
    @FXML private TableColumn<ManagingCommitteeEntity, String> mcDesignationCol;
    @FXML private TableColumn<ManagingCommitteeEntity, String> mcStartDateCol;
    @FXML private TableColumn<ManagingCommitteeEntity, String> mcEndDateCol;
    @FXML private TableColumn<ManagingCommitteeEntity, String> mcStatusCol;

    private final ObservableList<MeetingEntity> meetings = FXCollections.observableArrayList();
    private final ObservableList<ResolutionEntity> resolutions = FXCollections.observableArrayList();
    private final ObservableList<ManagingCommitteeEntity> committeeMembers = FXCollections.observableArrayList();

    public GovernanceController(
            NavigationManager navigationManager,
            GovernanceService governanceService,
            MemberRepository memberRepository) {
        super(navigationManager);
        this.governanceService = governanceService;
        this.memberRepository = memberRepository;
    }

    @FXML
    public void initialize() {
        // Meetings mapping
        meetTitleCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitle()));
        meetTypeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMeetingType()));
        meetDateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMeetingDate()));
        meetStatusCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus()));

        meetingTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            showMeetingDetails(newVal);
        });

        // Resolutions mapping
        resNumCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getResolutionNumber()));
        resSubjectCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSubject()));
        resMeetingCol.setCellValueFactory(c -> {
            MeetingEntity m = c.getValue().getMeeting();
            return new SimpleStringProperty(m != null ? m.toString() : "N/A");
        });
        resProposerCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getProposedBy()));
        resStatusCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus()));

        // Managing Committee mapping
        mcMemberCol.setCellValueFactory(c -> {
            MemberEntity m = c.getValue().getMember();
            return new SimpleStringProperty(m != null ? m.getFirstName() + " " + (m.getLastName() != null ? m.getLastName() : "") + " (" + m.getMemberNumber() + ")" : "N/A");
        });
        mcDesignationCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDesignation()));
        mcStartDateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStartDate()));
        mcEndDateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEndDate() != null ? c.getValue().getEndDate() : "-"));
        mcStatusCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus()));

        loadMeetings();
        loadResolutions();
        loadCommitteeMembers();
    }

    private void loadMeetings() {
        meetings.setAll(governanceService.getAllMeetings());
        meetingTable.setItems(meetings);
    }

    private void loadResolutions() {
        resolutions.setAll(governanceService.getAllResolutions());
        resolutionTable.setItems(resolutions);
    }

    private void loadCommitteeMembers() {
        committeeMembers.setAll(governanceService.getAllCommitteeMembers());
        mcTable.setItems(committeeMembers);
    }

    @FXML private Label meetTimeVal;
    @FXML private Label meetVenueVal;
    @FXML private Hyperlink meetOnlineLinkVal;

    private MeetingEntity selectedMeeting;

    private void showMeetingDetails(MeetingEntity meeting) {
        this.selectedMeeting = meeting;
        if (meeting == null) {
            meetTimeVal.setText("-");
            meetVenueVal.setText("-");
            meetOnlineLinkVal.setText("-");
            agendaArea.clear();
            minutesArea.clear();
            completeMeetingBtn.setDisable(true);
            return;
        }
        meetTimeVal.setText(meeting.getMeetingTime() != null ? meeting.getMeetingTime() : "-");
        meetVenueVal.setText(meeting.getVenue() != null ? meeting.getVenue() : "-");
        if (Boolean.TRUE.equals(meeting.getIsOnline()) && meeting.getOnlineLink() != null && !meeting.getOnlineLink().isBlank()) {
            meetOnlineLinkVal.setText(meeting.getOnlineLink());
            meetOnlineLinkVal.setDisable(false);
        } else {
            meetOnlineLinkVal.setText(Boolean.TRUE.equals(meeting.getIsOnline()) ? "Online Meeting (Link in Agenda)" : "-");
            meetOnlineLinkVal.setDisable(true);
        }

        agendaArea.setText(meeting.getAgenda() != null ? meeting.getAgenda() : "No Agenda Stated.");
        minutesArea.setText(meeting.getMinutes() != null ? meeting.getMinutes() : "No Minutes entered yet.");
        completeMeetingBtn.setDisable("COMPLETED".equals(meeting.getStatus()));
    }

    @FXML
    private void openOnlineLink() {
        if (selectedMeeting != null && selectedMeeting.getOnlineLink() != null && !selectedMeeting.getOnlineLink().isBlank()) {
            try {
                if (java.awt.Desktop.isDesktopSupported()) {
                    java.awt.Desktop.getDesktop().browse(new java.net.URI(selectedMeeting.getOnlineLink()));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @FXML
    private void handleScheduleMeeting() {
        Dialog<MeetingEntity> dialog = new Dialog<>();
        dialog.setTitle("Schedule Meeting");
        dialog.setHeaderText("Enter details to schedule a new meeting:");

        ButtonType scheduleBtnType = new ButtonType("Schedule", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(scheduleBtnType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField titleField = new TextField();
        titleField.setPromptText("e.g. AGM 2026");
        ComboBox<String> typeCombo = new ComboBox<>(FXCollections.observableArrayList("AGM", "SGM", "COMMITTEE"));
        typeCombo.getSelectionModel().selectFirst();
        
        DatePicker datePicker = new DatePicker(java.time.LocalDate.now());
        TextField timeField = new TextField();
        timeField.setPromptText("e.g. 10:30 AM");
        
        TextField venueField = new TextField();
        venueField.setPromptText("e.g. Clubhouse");

        CheckBox onlineCheckBox = new CheckBox("Hybrid / Online Meeting");
        TextField onlineLinkField = new TextField();
        onlineLinkField.setPromptText("Meeting link e.g. https://meet.google.com/abc-defg-hij");
        onlineLinkField.setVisible(false);

        onlineCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            onlineLinkField.setVisible(newVal);
        });

        TextArea agendaInput = new TextArea();
        agendaInput.setPromptText("Enter agendas here...");
        agendaInput.setPrefRowCount(3);

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Type:"), 0, 1);
        grid.add(typeCombo, 1, 1);
        grid.add(new Label("Date:"), 0, 2);
        grid.add(datePicker, 1, 2);
        grid.add(new Label("Time:"), 0, 3);
        grid.add(timeField, 1, 3);
        grid.add(new Label("Venue:"), 0, 4);
        grid.add(venueField, 1, 4);
        grid.add(onlineCheckBox, 0, 5);
        grid.add(onlineLinkField, 1, 5);
        grid.add(new Label("Agenda:"), 0, 6);
        grid.add(agendaInput, 1, 6);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == scheduleBtnType) {
                MeetingEntity m = new MeetingEntity();
                m.setTitle(titleField.getText().trim());
                m.setMeetingType(typeCombo.getValue());
                m.setMeetingDate(datePicker.getValue() != null ? datePicker.getValue().toString() : java.time.LocalDate.now().toString());
                m.setMeetingTime(timeField.getText().trim());
                m.setVenue(venueField.getText().trim());
                m.setIsOnline(onlineCheckBox.isSelected());
                m.setOnlineLink(onlineCheckBox.isSelected() ? onlineLinkField.getText().trim() : null);
                m.setAgenda(agendaInput.getText().trim());
                return m;
            }
            return null;
        });

        Optional<MeetingEntity> result = dialog.showAndWait();
        result.ifPresent(m -> {
            if (!m.getTitle().isEmpty() && !m.getMeetingDate().isEmpty()) {
                governanceService.scheduleMeeting(m);
                loadMeetings();
            }
        });
    }

    @FXML
    private void handleCompleteMeeting() {
        MeetingEntity selected = meetingTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Selection Required");
            alert.setHeaderText(null);
            alert.setContentText("Please select a meeting to save minutes.");
            alert.showAndWait();
            return;
        }

        String minutesText = minutesArea.getText() != null ? minutesArea.getText().trim() : "";
        governanceService.completeMeeting(selected.getId(), minutesText);

        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("Success");
        info.setHeaderText(null);
        info.setContentText("Minutes of the meeting saved successfully!");
        info.showAndWait();

        loadMeetings();
        MeetingEntity updated = governanceService.getAllMeetings().stream()
                .filter(m -> m.getId().equals(selected.getId()))
                .findFirst().orElse(selected);
        showMeetingDetails(updated);
    }

    @FXML
    private void handleMarkAttendance() {
        if (selectedMeeting == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Selection Required");
            alert.setHeaderText(null);
            alert.setContentText("Please select a meeting first.");
            alert.showAndWait();
            return;
        }

        List<ManagingCommitteeEntity> committee = governanceService.getAllCommitteeMembers();
        // Filter only ACTIVE committee members
        List<ManagingCommitteeEntity> activeCommittee = committee.stream()
                .filter(mc -> "ACTIVE".equalsIgnoreCase(mc.getStatus()))
                .toList();

        if (activeCommittee.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Active Committee Members");
            alert.setHeaderText(null);
            alert.setContentText("No active committee members found. Please add active committee members first.");
            alert.showAndWait();
            return;
        }

        List<com.society.governance.entity.MeetingAttendanceEntity> currentAttendance =
                governanceService.getAttendanceForMeeting(selectedMeeting.getId());
        java.util.Map<Integer, String> statusMap = new java.util.HashMap<>();
        for (com.society.governance.entity.MeetingAttendanceEntity a : currentAttendance) {
            statusMap.put(a.getMember().getId(), a.getStatus());
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Mark Committee Attendance");
        dialog.setHeaderText("Attendance for " + selectedMeeting.getTitle() + " (" + selectedMeeting.getMeetingDate() + ")");

        ButtonType saveBtnType = new ButtonType("Save Attendance", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtnType, ButtonType.CANCEL);

        VBox content = new VBox(10);
        content.setPadding(new Insets(15));

        java.util.Map<Integer, ComboBox<String>> comboMap = new java.util.HashMap<>();

        for (ManagingCommitteeEntity mc : activeCommittee) {
            MemberEntity m = mc.getMember();
            if (m == null) continue;

            HBox row = new HBox(15);
            row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            Label nameLabel = new Label(m.getFirstName() + " " + (m.getLastName() != null ? m.getLastName() : "") + " (" + mc.getDesignation() + ")");
            nameLabel.setPrefWidth(260);

            ComboBox<String> statusCombo = new ComboBox<>(FXCollections.observableArrayList("PRESENT", "ABSENT", "APOLOGY"));
            String currentStatus = statusMap.getOrDefault(m.getId(), "ABSENT");
            statusCombo.getSelectionModel().select(currentStatus);

            comboMap.put(m.getId(), statusCombo);
            row.getChildren().addAll(nameLabel, statusCombo);
            content.getChildren().add(row);
        }

        dialog.getDialogPane().setContent(new ScrollPane(content));

        dialog.setResultConverter(btn -> {
            if (btn == saveBtnType) {
                for (java.util.Map.Entry<Integer, ComboBox<String>> entry : comboMap.entrySet()) {
                    governanceService.markAttendance(selectedMeeting.getId(), entry.getKey(), entry.getValue().getValue());
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    @FXML
    private void handlePrintMeeting() {
        if (selectedMeeting == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Selection Required");
            alert.setHeaderText(null);
            alert.setContentText("Please select a meeting to print.");
            alert.showAndWait();
            return;
        }

        List<com.society.governance.entity.MeetingAttendanceEntity> attendanceList =
                governanceService.getAttendanceForMeeting(selectedMeeting.getId());

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><title>Meeting Report - ").append(selectedMeeting.getTitle()).append("</title>")
            .append("<style>")
            .append("@media print { @page { margin: 1.5cm; } }")
            .append("body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 30px; color: #1E293B; }")
            .append(".header { border-bottom: 2px solid #2563EB; padding-bottom: 12px; margin-bottom: 20px; }")
            .append(".title { font-size: 24px; font-weight: bold; color: #0F172A; margin: 0; }")
            .append(".meta-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; background: #F8FAFC; padding: 12px; border-radius: 6px; border: 1px solid #E2E8F0; margin-bottom: 20px; }")
            .append(".meta-item { font-size: 14px; }")
            .append(".meta-label { font-weight: bold; color: #64748B; }")
            .append(".section-title { font-size: 16px; font-weight: bold; color: #0F172A; margin-top: 20px; margin-bottom: 8px; border-left: 4px solid #2563EB; padding-left: 8px; }")
            .append(".box { background: #FFFFFF; border: 1px solid #CBD5E1; padding: 15px; border-radius: 6px; font-size: 14px; white-space: pre-wrap; line-height: 1.5; }")
            .append("table { width: 100%; border-collapse: collapse; margin-top: 8px; }")
            .append("th, td { border: 1px solid #CBD5E1; padding: 8px 12px; text-align: left; font-size: 14px; }")
            .append("th { background-color: #F1F5F9; font-weight: bold; }")
            .append(".present { color: #16A34A; font-weight: bold; }")
            .append(".absent { color: #DC2626; font-weight: bold; }")
            .append(".sig-box { margin-top: 50px; display: flex; justify-content: space-between; }")
            .append(".sig-line { width: 220px; border-top: 1px solid #0F172A; text-align: center; padding-top: 5px; font-weight: bold; }")
            .append("</style>")
            .append("</head><body onload='window.print()'>");

        html.append("<div class='header'>")
            .append("<div class='title'>").append(selectedMeeting.getTitle()).append(" - Official Meeting Report</div>")
            .append("</div>");

        html.append("<div class='meta-grid'>")
            .append("<div class='meta-item'><span class='meta-label'>Meeting Type:</span> ").append(selectedMeeting.getMeetingType()).append("</div>")
            .append("<div class='meta-item'><span class='meta-label'>Status:</span> ").append(selectedMeeting.getStatus()).append("</div>")
            .append("<div class='meta-item'><span class='meta-label'>Date & Time:</span> ").append(selectedMeeting.getMeetingDate())
            .append(selectedMeeting.getMeetingTime() != null ? " (" + selectedMeeting.getMeetingTime() + ")" : "").append("</div>")
            .append("<div class='meta-item'><span class='meta-label'>Venue:</span> ").append(selectedMeeting.getVenue()).append("</div>");

        if (Boolean.TRUE.equals(selectedMeeting.getIsOnline()) && selectedMeeting.getOnlineLink() != null) {
            html.append("<div class='meta-item' style='grid-column: span 2;'><span class='meta-label'>Online Link:</span> ").append(selectedMeeting.getOnlineLink()).append("</div>");
        }
        html.append("</div>");

        html.append("<div class='section-title'>Meeting Agenda</div>");
        html.append("<div class='box'>").append(selectedMeeting.getAgenda() != null && !selectedMeeting.getAgenda().isBlank() ? selectedMeeting.getAgenda() : "No Agenda Specified.").append("</div>");

        html.append("<div class='section-title'>Committee Attendance Register</div>");
        if (attendanceList.isEmpty()) {
            html.append("<div class='box'>No committee attendance recorded for this meeting.</div>");
        } else {
            html.append("<table><tr><th>Committee Officer Name</th><th>Attendance Status</th></tr>");
            for (com.society.governance.entity.MeetingAttendanceEntity a : attendanceList) {
                String name = a.getMember() != null ? a.getMember().getFirstName() + " " + (a.getMember().getLastName() != null ? a.getMember().getLastName() : "") : "Member #" + a.getMember().getId();
                String cssClass = "PRESENT".equalsIgnoreCase(a.getStatus()) ? "present" : "absent";
                html.append("<tr><td>").append(name).append("</td><td class='").append(cssClass).append("'>").append(a.getStatus()).append("</td></tr>");
            }
            html.append("</table>");
        }

        html.append("<div class='section-title'>Minutes of Meeting (MoM)</div>");
        html.append("<div class='box'>").append(selectedMeeting.getMinutes() != null && !selectedMeeting.getMinutes().isBlank() ? selectedMeeting.getMinutes() : "Minutes of meeting not recorded.").append("</div>");

        html.append("<div class='sig-box'>")
            .append("<div class='sig-line'>Chairman Signature</div>")
            .append("<div class='sig-line'>Secretary Signature</div>")
            .append("</div>");

        html.append("</body></html>");

        try {
            java.io.File tempFile = java.io.File.createTempFile("Meeting_Report_" + selectedMeeting.getId(), ".html");
            tempFile.deleteOnExit();
            java.nio.file.Files.writeString(tempFile.toPath(), html.toString());

            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().open(tempFile);
            }
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Print Error");
            alert.setHeaderText("Failed to generate meeting report");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleAddResolution() {
        Dialog<ResolutionEntity> dialog = new Dialog<>();
        dialog.setTitle("Record Resolution");
        dialog.setHeaderText("Add passed resolution details:");

        ButtonType addBtnType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addBtnType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField resNumField = new TextField();
        resNumField.setPromptText("e.g. RES-2026-01");
        TextField resSubField = new TextField();
        resSubField.setPromptText("Subject description");
        TextField proposerField = new TextField();
        proposerField.setPromptText("Name of proposer");
        ComboBox<MeetingEntity> meetingCombo = new ComboBox<>(meetings);

        grid.add(new Label("Resolution Number:"), 0, 0);
        grid.add(resNumField, 1, 0);
        grid.add(new Label("Subject:"), 0, 1);
        grid.add(resSubField, 1, 1);
        grid.add(new Label("Proposed By:"), 0, 2);
        grid.add(proposerField, 1, 2);
        grid.add(new Label("Link Meeting:"), 0, 3);
        grid.add(meetingCombo, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addBtnType) {
                ResolutionEntity r = new ResolutionEntity();
                r.setResolutionNumber(resNumField.getText().trim());
                r.setSubject(resSubField.getText().trim());
                r.setProposedBy(proposerField.getText().trim());
                r.setMeeting(meetingCombo.getValue());
                r.setStatus("PASSED");
                return r;
            }
            return null;
        });

        Optional<ResolutionEntity> result = dialog.showAndWait();
        result.ifPresent(r -> {
            if (!r.getResolutionNumber().isEmpty() && r.getMeeting() != null) {
                governanceService.createResolution(r);
                loadResolutions();
            }
        });
    }

    @FXML
    private void handleAddCommitteeMember() {
        List<MemberEntity> members = memberRepository.findAll();
        if (members.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Members Found");
            alert.setHeaderText(null);
            alert.setContentText("Please register members before appointing Managing Committee board members.");
            alert.showAndWait();
            return;
        }

        Dialog<ManagingCommitteeEntity> dialog = new Dialog<>();
        dialog.setTitle("Appoint Managing Committee Member");
        dialog.setHeaderText("Select Member & Board Designation:");

        ButtonType appointBtnType = new ButtonType("Appoint", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(appointBtnType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ComboBox<MemberEntity> memberCombo = new ComboBox<>(FXCollections.observableArrayList(members));
        memberCombo.getSelectionModel().selectFirst();

        ComboBox<String> designationCombo = new ComboBox<>(FXCollections.observableArrayList(
                "CHAIRMAN", "SECRETARY", "TREASURER", "COMMITTEE_MEMBER", "INTERNAL_AUDITOR"
        ));
        designationCombo.getSelectionModel().selectFirst();

        DatePicker startDatePicker = new DatePicker(LocalDate.now());
        DatePicker endDatePicker = new DatePicker(LocalDate.now().plusYears(3)); // Standard 3-year term

        grid.add(new Label("Select Member:"), 0, 0); grid.add(memberCombo, 1, 0);
        grid.add(new Label("Designation:"), 0, 1); grid.add(designationCombo, 1, 1);
        grid.add(new Label("Start Date:"), 0, 2); grid.add(startDatePicker, 1, 2);
        grid.add(new Label("End Date:"), 0, 3); grid.add(endDatePicker, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == appointBtnType) {
                ManagingCommitteeEntity mc = new ManagingCommitteeEntity();
                mc.setMember(memberCombo.getValue());
                mc.setDesignation(designationCombo.getValue());
                mc.setStartDate(startDatePicker.getValue() != null ? startDatePicker.getValue().toString() : LocalDate.now().toString());
                mc.setEndDate(endDatePicker.getValue() != null ? endDatePicker.getValue().toString() : null);
                mc.setStatus("ACTIVE");
                return mc;
            }
            return null;
        });

        Optional<ManagingCommitteeEntity> result = dialog.showAndWait();
        result.ifPresent(mc -> {
            if (mc.getMember() != null) {
                governanceService.addCommitteeMember(mc);
                loadCommitteeMembers();
            }
        });
    }
}
