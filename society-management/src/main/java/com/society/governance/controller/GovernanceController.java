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

    private void showMeetingDetails(MeetingEntity meeting) {
        if (meeting == null) {
            agendaArea.clear();
            minutesArea.clear();
            completeMeetingBtn.setDisable(true);
            return;
        }
        agendaArea.setText(meeting.getAgenda() != null ? meeting.getAgenda() : "No Agenda Stated.");
        minutesArea.setText(meeting.getMinutes() != null ? meeting.getMinutes() : "No Minutes entered yet.");
        completeMeetingBtn.setDisable("COMPLETED".equals(meeting.getStatus()));
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
        TextField dateField = new TextField();
        dateField.setPromptText("YYYY-MM-DD");
        TextField venueField = new TextField();
        venueField.setPromptText("e.g. Clubhouse");
        TextArea agendaInput = new TextArea();
        agendaInput.setPromptText("Enter agendas here...");
        agendaInput.setPrefRowCount(4);

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Type:"), 0, 1);
        grid.add(typeCombo, 1, 1);
        grid.add(new Label("Date:"), 0, 2);
        grid.add(dateField, 1, 2);
        grid.add(new Label("Venue:"), 0, 3);
        grid.add(venueField, 1, 3);
        grid.add(new Label("Agenda:"), 0, 4);
        grid.add(agendaInput, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == scheduleBtnType) {
                MeetingEntity m = new MeetingEntity();
                m.setTitle(titleField.getText().trim());
                m.setMeetingType(typeCombo.getValue());
                m.setMeetingDate(dateField.getText().trim());
                m.setVenue(venueField.getText().trim());
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
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Complete Meeting & Enter Minutes");
        dialog.setHeaderText("Record Minutes for " + selected.getTitle());
        dialog.setContentText("Enter Minutes text:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(minutes -> {
            governanceService.completeMeeting(selected.getId(), minutes);
            loadMeetings();
            showMeetingDetails(selected);
        });
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
