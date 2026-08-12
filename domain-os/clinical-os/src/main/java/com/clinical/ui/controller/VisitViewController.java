package com.clinical.ui.controller;

import com.clinical.doctor.entity.DoctorEntity;
import com.clinical.doctor.service.DoctorService;
import com.clinical.patient.entity.PatientEntity;
import com.clinical.patient.service.PatientService;
import com.clinical.visit.entity.VisitEntity;
import com.clinical.visit.service.VisitService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class VisitViewController {

    private final VisitService visitService;
    private final PatientService patientService;
    private final DoctorService doctorService;

    @FXML private TableView<VisitQueueRow> visitQueueTable;
    @FXML private TableColumn<VisitQueueRow, String> visitIdCol;
    @FXML private TableColumn<VisitQueueRow, String> patientNameCol;
    @FXML private TableColumn<VisitQueueRow, String> patientPhoneCol;
    @FXML private TableColumn<VisitQueueRow, String> patientIdCol;
    @FXML private TableColumn<VisitQueueRow, String> doctorCol;
    @FXML private TableColumn<VisitQueueRow, String> statusCol;

    @FXML private ComboBox<PatientEntity> patientSelectCombo;
    @FXML private ComboBox<DoctorEntity> doctorSelectCombo;
    @FXML private ComboBox<String> visitTypeCombo;

    private List<PatientEntity> allPatients = new ArrayList<>();

    public VisitViewController(VisitService visitService, PatientService patientService, DoctorService doctorService) {
        this.visitService = visitService;
        this.patientService = patientService;
        this.doctorService = doctorService;
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        loadPatients();
        loadDoctors();
        setupVisitTypes();
        loadQueue();
    }

    private void setupTableColumns() {
        if (visitIdCol != null) {
            visitIdCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getVisitId()));
            patientNameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPatientName()));
            patientPhoneCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPatientPhone()));
            patientIdCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPatientId()));
            doctorCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDoctorName()));
            statusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));
        }
    }

    private void loadPatients() {
        if (patientSelectCombo == null) return;
        allPatients = patientService.getAllPatients();
        patientSelectCombo.setItems(FXCollections.observableArrayList(allPatients));
        patientSelectCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(PatientEntity p) {
                if (p == null) return "";
                String phone = p.getPhone() != null ? p.getPhone() : "";
                return p.getFirstName() + " " + p.getLastName() + " (" + p.getPatientId() + ") - " + phone;
            }

            @Override
            public PatientEntity fromString(String string) {
                if (string == null || string.isBlank()) return null;
                return allPatients.stream()
                        .filter(p -> toString(p).equalsIgnoreCase(string))
                        .findFirst()
                        .orElse(null);
            }
        });
    }

    private void loadDoctors() {
        if (doctorSelectCombo == null) return;
        List<DoctorEntity> doctors = doctorService.getAllDoctors();
        doctorSelectCombo.setItems(FXCollections.observableArrayList(doctors));
        doctorSelectCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(DoctorEntity doc) {
                if (doc == null) return "";
                String name = doc.getDisplayName() != null ? doc.getDisplayName() :
                        (doc.getName() != null ? doc.getName() : ("Dr. " + doc.getFirstName() + " " + doc.getLastName()));
                String spec = (doc.getSpecialization() != null && !doc.getSpecialization().isEmpty()) ?
                        " (" + doc.getSpecialization() + ")" : "";
                return name + spec;
            }

            @Override
            public DoctorEntity fromString(String string) {
                return null;
            }
        });
        if (!doctors.isEmpty()) {
            doctorSelectCombo.getSelectionModel().selectFirst();
        }
    }

    private void setupVisitTypes() {
        if (visitTypeCombo != null) {
            visitTypeCombo.setItems(FXCollections.observableArrayList("OPD", "Follow-up", "Emergency", "Routine Checkup"));
            visitTypeCombo.getSelectionModel().selectFirst();
        }
    }

    @FXML
    public void handleCreateVisit() {
        PatientEntity selectedPatient = patientSelectCombo != null ? patientSelectCombo.getValue() : null;
        if (selectedPatient == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a valid patient to create a visit.", ButtonType.OK);
            alert.show();
            return;
        }

        DoctorEntity selectedDoctor = doctorSelectCombo != null ? doctorSelectCombo.getValue() : null;
        String doctorName = selectedDoctor != null ?
                (selectedDoctor.getDisplayName() != null ? selectedDoctor.getDisplayName() : selectedDoctor.getName()) : "General Practitioner";
        String visitType = visitTypeCombo != null && visitTypeCombo.getValue() != null ? visitTypeCombo.getValue() : "OPD";

        visitService.createVisit(selectedPatient.getPatientId(), doctorName, visitType);
        loadQueue();
        if (patientSelectCombo != null) patientSelectCombo.setValue(null);
    }

    @FXML
    public void handleQuickRegisterPatient() {
        Dialog<PatientEntity> dialog = new Dialog<>();
        dialog.setTitle("Quick Patient Registration");
        dialog.setHeaderText("Register New Patient for OPD Visit");

        ButtonType saveButtonType = new ButtonType("Register & Select", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First Name");
        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");
        TextField phoneField = new TextField();
        phoneField.setPromptText("Mobile Number");
        ComboBox<String> genderCombo = new ComboBox<>(FXCollections.observableArrayList("Male", "Female", "Other"));
        genderCombo.getSelectionModel().selectFirst();
        TextField ageField = new TextField();
        ageField.setPromptText("Age (e.g. 35)");

        grid.add(new Label("First Name:"), 0, 0);
        grid.add(firstNameField, 1, 0);
        grid.add(new Label("Last Name:"), 0, 1);
        grid.add(lastNameField, 1, 1);
        grid.add(new Label("Mobile No:"), 0, 2);
        grid.add(phoneField, 1, 2);
        grid.add(new Label("Gender:"), 0, 3);
        grid.add(genderCombo, 1, 3);
        grid.add(new Label("Age:"), 0, 4);
        grid.add(ageField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                if (firstNameField.getText().trim().isEmpty() || phoneField.getText().trim().isEmpty()) {
                    return null;
                }
                PatientEntity patient = new PatientEntity();
                patient.setFirstName(firstNameField.getText().trim());
                patient.setLastName(lastNameField.getText().trim().isEmpty() ? "." : lastNameField.getText().trim());
                patient.setPhone(phoneField.getText().trim());
                patient.setGender(genderCombo.getValue());
                try {
                    if (!ageField.getText().trim().isEmpty()) {
                        int age = Integer.parseInt(ageField.getText().trim());
                        patient.setDob(LocalDate.now().minusYears(age));
                    }
                } catch (Exception ignored) {}
                return patient;
            }
            return null;
        });

        Optional<PatientEntity> result = dialog.showAndWait();
        result.ifPresent(p -> {
            PatientEntity registered = patientService.registerPatient(p);
            loadPatients();
            patientSelectCombo.setValue(registered);
        });
    }

    private void loadQueue() {
        if (visitQueueTable == null) return;
        List<VisitEntity> queue = visitService.getQueueByStatus("WAITING");
        Map<String, PatientEntity> patientCache = patientService.getAllPatients().stream()
                .collect(Collectors.toMap(PatientEntity::getPatientId, p -> p, (p1, p2) -> p1));

        List<VisitQueueRow> rows = queue.stream().map(v -> {
            PatientEntity p = patientCache.get(v.getPatientId());
            String pName = (p != null) ? (p.getFirstName() + " " + p.getLastName()) : "Unknown Patient";
            String pPhone = (p != null && p.getPhone() != null) ? p.getPhone() : "N/A";
            return new VisitQueueRow(v.getVisitId(), pName, pPhone, v.getPatientId(), v.getDoctorName(), v.getStatus());
        }).collect(Collectors.toList());

        visitQueueTable.setItems(FXCollections.observableArrayList(rows));
    }

    public static class VisitQueueRow {
        private final String visitId;
        private final String patientName;
        private final String patientPhone;
        private final String patientId;
        private final String doctorName;
        private final String status;

        public VisitQueueRow(String visitId, String patientName, String patientPhone, String patientId, String doctorName, String status) {
            this.visitId = visitId;
            this.patientName = patientName;
            this.patientPhone = patientPhone;
            this.patientId = patientId;
            this.doctorName = doctorName;
            this.status = status;
        }

        public String getVisitId() { return visitId; }
        public String getPatientName() { return patientName; }
        public String getPatientPhone() { return patientPhone; }
        public String getPatientId() { return patientId; }
        public String getDoctorName() { return doctorName; }
        public String getStatus() { return status; }
    }
}

