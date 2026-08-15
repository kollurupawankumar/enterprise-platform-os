package com.clinical.ui.controller;

import com.clinical.appointment.entity.AppointmentEntity;
import com.clinical.appointment.service.AppointmentService;
import com.clinical.doctor.entity.DoctorEntity;
import com.clinical.doctor.service.DoctorService;
import com.clinical.patient.entity.PatientEntity;
import com.clinical.patient.service.PatientService;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class AppointmentViewController {

    private final AppointmentService appointmentService;
    private final DoctorService doctorService;
    private final PatientService patientService;

    // FXML Controls
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> doctorCombo;
    @FXML private ComboBox<String> statusFilterCombo;
    @FXML private VBox slotsContainer;

    @FXML private TableView<AppointmentEntity> appointmentsTable;
    @FXML private TableColumn<AppointmentEntity, String> colAptId;
    @FXML private TableColumn<AppointmentEntity, String> colTime;
    @FXML private TableColumn<AppointmentEntity, String> colToken;
    @FXML private TableColumn<AppointmentEntity, String> colPatient;
    @FXML private TableColumn<AppointmentEntity, String> colMobile;
    @FXML private TableColumn<AppointmentEntity, String> colDoctor;
    @FXML private TableColumn<AppointmentEntity, String> colStatus;
    @FXML private TableColumn<AppointmentEntity, String> colNotes;

    private final ObservableList<AppointmentEntity> appointmentList = FXCollections.observableArrayList();
    private final String[] TIME_SLOTS = {
            "09:00 AM", "09:15 AM", "09:30 AM", "09:45 AM",
            "10:00 AM", "10:15 AM", "10:30 AM", "10:45 AM",
            "11:00 AM", "11:15 AM", "11:30 AM", "11:45 AM",
            "02:00 PM", "02:15 PM", "02:30 PM", "02:45 PM",
            "03:00 PM", "03:15 PM", "03:30 PM", "03:45 PM",
            "04:00 PM", "04:15 PM", "04:30 PM", "04:45 PM"
    };

    public AppointmentViewController(AppointmentService appointmentService,
                                     DoctorService doctorService,
                                     PatientService patientService) {
        this.appointmentService = appointmentService;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    @FXML
    public void initialize() {
        if (datePicker != null) {
            datePicker.setValue(LocalDate.now());
            datePicker.valueProperty().addListener((obs, oldVal, newVal) -> handleFilterAppointments());
        }

        if (statusFilterCombo != null) {
            statusFilterCombo.setItems(FXCollections.observableArrayList("ALL", "BOOKED", "CHECKED_IN", "CANCELLED"));
            statusFilterCombo.setValue("ALL");
            statusFilterCombo.valueProperty().addListener((obs, oldVal, newVal) -> handleFilterAppointments());
        }

        loadDoctorsCombo();
        if (doctorCombo != null) {
            doctorCombo.valueProperty().addListener((obs, oldVal, newVal) -> handleFilterAppointments());
        }

        setupTable();
        handleFilterAppointments();
    }

    private void loadDoctorsCombo() {
        if (doctorCombo == null) return;
        List<DoctorEntity> docs = doctorService.getAllDoctors();
        ObservableList<String> options = FXCollections.observableArrayList();
        options.add("ALL - All Doctors");
        for (DoctorEntity doc : docs) {
            options.add(doc.getDoctorId() + " - " + doc.getName() + " (" + doc.getSpecialization() + ")");
        }
        doctorCombo.setItems(options);
        doctorCombo.setValue("ALL - All Doctors");
    }

    private void setupTable() {
        if (appointmentsTable == null) return;
        colAptId.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("slotTime"));
        colToken.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTokenNumber() != null ? "Token #" + data.getValue().getTokenNumber() : "-"));
        colPatient.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPatientName() + " (" + data.getValue().getPatientId() + ")"));
        colMobile.setCellValueFactory(new PropertyValueFactory<>("mobileNumber"));
        colDoctor.setCellValueFactory(new PropertyValueFactory<>("doctorName"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colNotes.setCellValueFactory(new PropertyValueFactory<>("notes"));

        appointmentsTable.setItems(appointmentList);
    }

    @FXML
    public void handleFilterAppointments() {
        LocalDate selectedDate = datePicker != null && datePicker.getValue() != null ? datePicker.getValue() : LocalDate.now();
        String docSelection = doctorCombo != null && doctorCombo.getValue() != null ? doctorCombo.getValue() : "ALL";
        String docId = docSelection.split(" - ")[0].trim();

        List<AppointmentEntity> appointments = appointmentService.getAppointmentsByDateAndDoctor(selectedDate, docId);

        String statusFilter = statusFilterCombo != null ? statusFilterCombo.getValue() : "ALL";
        if (statusFilter != null && !"ALL".equalsIgnoreCase(statusFilter)) {
            appointments = appointments.stream().filter(a -> statusFilter.equalsIgnoreCase(a.getStatus())).toList();
        }

        appointmentList.setAll(appointments);
        renderSlotGrid(selectedDate, docId, appointments);
    }

    private void renderSlotGrid(LocalDate date, String doctorId, List<AppointmentEntity> appointments) {
        if (slotsContainer == null) return;
        slotsContainer.getChildren().clear();

        for (String slot : TIME_SLOTS) {
            HBox slotRow = new HBox(12);
            slotRow.setAlignment(Pos.CENTER_LEFT);
            slotRow.setPadding(new Insets(8, 12, 8, 12));
            slotRow.setStyle("-fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: #E2E8F0;");

            Label timeLabel = new Label(slot);
            timeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-min-width: 80px;");

            AppointmentEntity matchedApt = appointments.stream()
                    .filter(a -> slot.equalsIgnoreCase(a.getSlotTime()))
                    .findFirst().orElse(null);

            if (matchedApt != null) {
                Label infoLabel = new Label(matchedApt.getPatientName() + " | Token #" + (matchedApt.getTokenNumber() != null ? matchedApt.getTokenNumber() : "Pending"));
                infoLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #1E293B;");
                HBox.setHgrow(infoLabel, Priority.ALWAYS);

                Label statusBadge = new Label(matchedApt.getStatus());
                if ("CHECKED_IN".equalsIgnoreCase(matchedApt.getStatus())) {
                    statusBadge.setStyle("-fx-background-color: #D1FAE5; -fx-text-fill: #065F46; -fx-font-weight: bold; -fx-padding: 3 8; -fx-background-radius: 4;");
                    slotRow.setStyle("-fx-background-color: #ECFDF5; -fx-border-color: #A7F3D0; -fx-background-radius: 6;");
                } else if ("CANCELLED".equalsIgnoreCase(matchedApt.getStatus())) {
                    statusBadge.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #991B1B; -fx-font-weight: bold; -fx-padding: 3 8; -fx-background-radius: 4;");
                    slotRow.setStyle("-fx-background-color: #FEF2F2; -fx-border-color: #FCA5A5; -fx-background-radius: 6;");
                } else {
                    statusBadge.setStyle("-fx-background-color: #DBEAFE; -fx-text-fill: #1E40AF; -fx-font-weight: bold; -fx-padding: 3 8; -fx-background-radius: 4;");
                    slotRow.setStyle("-fx-background-color: #EFF6FF; -fx-border-color: #BFDBFE; -fx-background-radius: 6;");
                }

                slotRow.getChildren().addAll(timeLabel, infoLabel, statusBadge);
            } else {
                Label availLabel = new Label("🟢 Available Time-Slot");
                availLabel.setStyle("-fx-text-fill: #10B981; -fx-font-weight: bold;");
                HBox.setHgrow(availLabel, Priority.ALWAYS);

                Button bookBtn = new Button("Book Slot");
                bookBtn.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #334155; -fx-font-weight: bold; -fx-cursor: hand;");
                bookBtn.setOnAction(e -> handleBookSpecificSlot(slot));

                slotRow.setStyle("-fx-background-color: #FAFAFA; -fx-border-color: #E2E8F0; -fx-background-radius: 6;");
                slotRow.getChildren().addAll(timeLabel, availLabel, bookBtn);
            }

            slotsContainer.getChildren().add(slotRow);
        }
    }

    @FXML
    public void handleOpenBookingDialog() {
        handleBookSpecificSlot("10:00 AM");
    }

    private void handleBookSpecificSlot(String defaultSlot) {
        Dialog<AppointmentEntity> dialog = new Dialog<>();
        dialog.setTitle("Schedule Doctor Appointment");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        ComboBox<String> patientCombo = new ComboBox<>();
        List<PatientEntity> patients = patientService.getAllPatients();
        ObservableList<String> pOptions = FXCollections.observableArrayList();
        for (PatientEntity p : patients) {
            pOptions.add(p.getPatientId() + " - " + p.getFirstName() + " " + p.getLastName() + " (" + p.getPhone() + ")");
        }
        patientCombo.setItems(pOptions);
        patientCombo.setPrefWidth(260);
        if (!pOptions.isEmpty()) patientCombo.getSelectionModel().select(0);

        DatePicker aptDate = new DatePicker(datePicker.getValue() != null ? datePicker.getValue() : LocalDate.now());
        
        ComboBox<String> slotCombo = new ComboBox<>(FXCollections.observableArrayList(TIME_SLOTS));
        slotCombo.setValue(defaultSlot);

        ComboBox<String> docCombo = new ComboBox<>();
        List<DoctorEntity> docs = doctorService.getAllDoctors();
        ObservableList<String> dOptions = FXCollections.observableArrayList();
        for (DoctorEntity d : docs) {
            dOptions.add(d.getDoctorId() + " - " + d.getName() + " (" + d.getSpecialization() + ")");
        }
        docCombo.setItems(dOptions);
        if (!dOptions.isEmpty()) docCombo.getSelectionModel().select(0);

        TextField notesField = new TextField();
        notesField.setPromptText("Reason for visit / symptoms");

        grid.add(new Label("Select Patient:"), 0, 0);
        grid.add(patientCombo, 1, 0);
        grid.add(new Label("Appointment Date:"), 0, 1);
        grid.add(aptDate, 1, 1);
        grid.add(new Label("Time Slot:"), 0, 2);
        grid.add(slotCombo, 1, 2);
        grid.add(new Label("Attending Doctor:"), 0, 3);
        grid.add(docCombo, 1, 3);
        grid.add(new Label("Notes / Symptoms:"), 0, 4);
        grid.add(notesField, 1, 4);

        dialog.getDialogPane().setContent(grid);
        ButtonType bookBtnType = new ButtonType("Book Appointment", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(bookBtnType, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == bookBtnType && patientCombo.getValue() != null) {
                String pSel = patientCombo.getValue();
                String pId = pSel.split(" - ")[0].trim();
                PatientEntity p = patientService.findByPatientId(pId).orElse(null);

                String dSel = docCombo.getValue();
                String dId = dSel != null ? dSel.split(" - ")[0].trim() : "DOC-001";
                String dName = dSel != null ? dSel.split(" - ")[1].split(" \\(")[0].trim() : "Doctor";

                AppointmentEntity apt = new AppointmentEntity();
                apt.setPatientId(pId);
                apt.setPatientName(p != null ? p.getFirstName() + " " + p.getLastName() : pId);
                apt.setMobileNumber(p != null ? p.getPhone() : "");
                apt.setDoctorId(dId);
                apt.setDoctorName(dName);
                apt.setAppointmentDate(aptDate.getValue());
                apt.setSlotTime(slotCombo.getValue());
                apt.setNotes(notesField.getText());
                apt.setStatus("BOOKED");
                return apt;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(apt -> {
            appointmentService.bookAppointment(apt);
            handleFilterAppointments();
            showAlert(Alert.AlertType.INFORMATION, "Appointment Booked", "Appointment " + apt.getAppointmentId() + " scheduled for " + apt.getSlotTime() + " successfully!");
        });
    }

    @FXML
    public void handleCheckInSelected() {
        AppointmentEntity selected = appointmentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Appointment Selected", "Please select an appointment from the table to check-in.");
            return;
        }

        AppointmentEntity updated = appointmentService.checkInAppointment(selected.getAppointmentId());
        handleFilterAppointments();
        showAlert(Alert.AlertType.INFORMATION, "Patient Checked-In", "Patient " + updated.getPatientName() + " checked-in successfully! Assigned Token #" + updated.getTokenNumber() + " into Doctor OPD Queue.");
    }

    @FXML
    public void handleCancelSelected() {
        AppointmentEntity selected = appointmentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Appointment Selected", "Please select an appointment from the table to cancel.");
            return;
        }

        appointmentService.updateStatus(selected.getAppointmentId(), "CANCELLED");
        handleFilterAppointments();
        showAlert(Alert.AlertType.INFORMATION, "Appointment Cancelled", "Appointment " + selected.getAppointmentId() + " has been cancelled.");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
