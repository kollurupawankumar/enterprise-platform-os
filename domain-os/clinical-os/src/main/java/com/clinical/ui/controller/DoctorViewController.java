package com.clinical.ui.controller;

import com.clinical.doctor.entity.ClinicalEncounterEntity;
import com.clinical.doctor.entity.DoctorEntity;
import com.clinical.doctor.entity.ReferralEntity;
import com.clinical.doctor.repository.ClinicalEncounterRepository;
import com.clinical.doctor.service.DoctorConsultationService;
import com.clinical.doctor.service.DoctorService;
import com.clinical.doctor.service.ReferralService;
import com.clinical.lab.entity.LabOrderEntity;
import com.clinical.lab.repository.LabOrderRepository;
import com.clinical.lab.service.LabDiagnosticsService;
import com.clinical.patient.entity.PatientEntity;
import com.clinical.patient.service.PatientService;
import com.clinical.prescription.entity.PrescriptionEntity;
import com.clinical.prescription.entity.PrescriptionItemEntity;
import com.clinical.prescription.repository.PrescriptionRepository;
import com.clinical.prescription.service.PrescriptionService;
import com.clinical.visit.entity.VisitEntity;
import com.clinical.visit.service.VisitService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class DoctorViewController {

    private final VisitService visitService;
    private final DoctorConsultationService consultationService;
    private final PrescriptionService prescriptionService;
    private final LabDiagnosticsService labService;
    private final ReferralService referralService;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final ClinicalEncounterRepository encounterRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final LabOrderRepository labOrderRepository;

    @FXML private ComboBox<String> doctorFilterCombo;
    @FXML private ComboBox<String> visitQueueCombo;
    @FXML private Label patientSummaryLabel;
    @FXML private Label allergiesAlertLabel;

    // Vitals & History
    @FXML private TextField chiefComplaintField;
    @FXML private TextField systolicBpField;
    @FXML private TextField diastolicBpField;
    @FXML private TextField pulseField;
    @FXML private TextField tempField;
    @FXML private TextField spo2Field;
    @FXML private TextField weightField;
    @FXML private TextField heightField;
    @FXML private Label bmiLabel;
    @FXML private TextArea historyArea;

    // Examination & Assessment
    @FXML private TextArea examArea;
    @FXML private TextArea assessmentArea;

    // Diagnosis
    @FXML private TextField icdCodeField;
    @FXML private TextField icdDescField;
    @FXML private TextField diagnosisField;

    // Orders & Referral
    @FXML private TextField medNameField;
    @FXML private TextField medDosageField;
    @FXML private TextField medDurationField;
    @FXML private ListView<String> prescriptionListView;

    @FXML private TextField labTestNameField;
    @FXML private ListView<String> labOrderListView;

    @FXML private TextField refSpecialtyField;
    @FXML private TextField refHospitalField;
    @FXML private TextField refReasonField;

    // Patient History Tab Controls
    @FXML private ListView<String> historyEncountersListView;
    @FXML private Label historyVitalsLabel;
    @FXML private Label historyNotesLabel;
    @FXML private ListView<String> historyPrescriptionListView;
    @FXML private ListView<String> historyLabListView;

    // My Consultations & Edit Tab Controls
    @FXML private TableView<DoctorVisitRow> doctorVisitsTable;
    @FXML private TableColumn<DoctorVisitRow, String> docVisitIdCol;
    @FXML private TableColumn<DoctorVisitRow, String> docVisitDateCol;
    @FXML private TableColumn<DoctorVisitRow, String> docPatientCol;
    @FXML private TableColumn<DoctorVisitRow, String> docDiagnosisCol;
    @FXML private TableColumn<DoctorVisitRow, String> docStatusCol;

    private VisitEntity activeVisit;
    private PatientEntity activePatient;
    private final List<PrescriptionItemEntity> tempPrescriptionItems = new ArrayList<>();
    private final List<String> tempLabTests = new ArrayList<>();
    private List<PrescriptionItemEntity> selectedHistoryPrescriptionItems = new ArrayList<>();

    public DoctorViewController(VisitService visitService,
                                DoctorConsultationService consultationService,
                                PrescriptionService prescriptionService,
                                LabDiagnosticsService labService,
                                ReferralService referralService,
                                PatientService patientService,
                                DoctorService doctorService,
                                ClinicalEncounterRepository encounterRepository,
                                PrescriptionRepository prescriptionRepository,
                                LabOrderRepository labOrderRepository) {
        this.visitService = visitService;
        this.consultationService = consultationService;
        this.prescriptionService = prescriptionService;
        this.labService = labService;
        this.referralService = referralService;
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.encounterRepository = encounterRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.labOrderRepository = labOrderRepository;
    }

    @FXML
    public void initialize() {
        setupDoctorFilter();
        loadVisitQueue();
        setupBmiListener();
        setupHistorySelectionListener();
        setupDoctorVisitsTable();
        loadDoctorVisits();
    }

    private void setupDoctorFilter() {
        if (doctorFilterCombo == null) return;
        List<String> doctorsList = new ArrayList<>();
        doctorsList.add("All Doctors");
        doctorService.getAllDoctors().forEach(d -> {
            String name = d.getDisplayName() != null ? d.getDisplayName() : d.getName();
            doctorsList.add(name);
        });
        doctorFilterCombo.setItems(FXCollections.observableArrayList(doctorsList));
        doctorFilterCombo.getSelectionModel().selectFirst();

        doctorFilterCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            loadVisitQueue();
            loadDoctorVisits();
        });
    }

    private void loadVisitQueue() {
        if (visitQueueCombo == null) return;
        List<VisitEntity> activeVisits = visitService.getActiveVisits();
        String selectedDoctor = doctorFilterCombo != null ? doctorFilterCombo.getValue() : "All Doctors";

        if (selectedDoctor != null && !"All Doctors".equals(selectedDoctor)) {
            activeVisits = activeVisits.stream()
                    .filter(v -> selectedDoctor.equalsIgnoreCase(v.getDoctorName()))
                    .collect(Collectors.toList());
        }

        Map<String, PatientEntity> patientCache = patientService.getAllPatients().stream()
                .collect(Collectors.toMap(PatientEntity::getPatientId, p -> p, (p1, p2) -> p1));

        List<String> displayList = activeVisits.stream().map(v -> {
            PatientEntity p = patientCache.get(v.getPatientId());
            String pName = (p != null) ? (p.getFirstName() + " " + p.getLastName()) : v.getPatientId();
            return v.getVisitId() + " - " + pName + " (" + v.getPatientId() + ")";
        }).collect(Collectors.toList());

        visitQueueCombo.setItems(FXCollections.observableArrayList(displayList));
    }

    private void setupBmiListener() {
        if (weightField != null && heightField != null) {
            weightField.textProperty().addListener((o, oldV, newV) -> calculateBmi());
            heightField.textProperty().addListener((o, oldV, newV) -> calculateBmi());
        }
    }

    private void calculateBmi() {
        try {
            double weight = Double.parseDouble(weightField.getText());
            double heightM = Double.parseDouble(heightField.getText()) / 100.0;
            if (heightM > 0) {
                double bmi = weight / (heightM * heightM);
                if (bmiLabel != null) bmiLabel.setText(String.format("%.2f", bmi));
            }
        } catch (Exception e) {
            if (bmiLabel != null) bmiLabel.setText("--.--");
        }
    }

    @FXML
    public void handleLoadVisit() {
        String selected = visitQueueCombo != null ? visitQueueCombo.getValue() : null;
        if (selected == null) return;

        String visitId = selected.split(" - ")[0];
        visitService.getActiveVisits().stream()
                .filter(v -> v.getVisitId().equals(visitId))
                .findFirst()
                .ifPresent(v -> {
                    this.activeVisit = v;
                    loadPatientDetails(v.getPatientId());
                    loadPatientHistoryTab(v.getPatientId());
                    encounterRepository.findByVisitId(visitId).ifPresent(this::populateEncounterToForm);
                });
    }

    private void loadPatientDetails(String patientId) {
        Optional<PatientEntity> optP = patientService.findByPatientId(patientId);
        if (optP.isPresent()) {
            this.activePatient = optP.get();
            String ageStr = activePatient.getCalculatedAge() != null ? activePatient.getCalculatedAge() + " Yrs" : "N/A";
            if (patientSummaryLabel != null) {
                patientSummaryLabel.setText("Patient: " + activePatient.getFirstName() + " " + activePatient.getLastName() +
                        " | " + ageStr + " | " + activePatient.getGender() + " | Mobile: " + activePatient.getPhone() +
                        " (ID: " + activePatient.getPatientId() + ")");
            }
            if (allergiesAlertLabel != null) {
                String allergies = activePatient.getAllergies();
                if (allergies != null && !allergies.trim().isEmpty() && !"None".equalsIgnoreCase(allergies)) {
                    allergiesAlertLabel.setText("⚠️ Allergies: " + allergies);
                    allergiesAlertLabel.setStyle("-fx-background-color: #FEF2F2; -fx-text-fill: #DC2626; -fx-padding: 4 10 4 10; -fx-background-radius: 4; -fx-font-weight: bold;");
                } else {
                    allergiesAlertLabel.setText("✓ Allergies: None Recorded");
                    allergiesAlertLabel.setStyle("-fx-background-color: #F0FDF4; -fx-text-fill: #166534; -fx-padding: 4 10 4 10; -fx-background-radius: 4; -fx-font-weight: bold;");
                }
            }
        } else {
            if (patientSummaryLabel != null) patientSummaryLabel.setText("Active Patient ID: " + patientId);
        }
    }

    private void loadPatientHistoryTab(String patientId) {
        if (historyEncountersListView == null) return;
        List<ClinicalEncounterEntity> pastEncounters = encounterRepository.findByPatientId(patientId);
        List<String> listItems = pastEncounters.stream().map(e -> {
            String dateStr = e.getCreatedAt() != null ? e.getCreatedAt().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm")) : "N/A";
            return e.getVisitId() + " | " + dateStr + " | " + e.getDiagnosis();
        }).collect(Collectors.toList());

        historyEncountersListView.setItems(FXCollections.observableArrayList(listItems));
    }

    private void setupHistorySelectionListener() {
        if (historyEncountersListView == null) return;
        historyEncountersListView.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV == null) return;
            String visitId = newV.split(" \\| ")[0];
            encounterRepository.findByVisitId(visitId).ifPresent(this::displayHistoryDetails);
        });
    }

    private void displayHistoryDetails(ClinicalEncounterEntity enc) {
        if (historyVitalsLabel != null) {
            historyVitalsLabel.setText("Vitals Recorded: BP " + (enc.getSystolicBp() != null ? enc.getSystolicBp() + "/" + enc.getDiastolicBp() : "--/--") +
                    " mmHg | Pulse: " + (enc.getPulseRate() != null ? enc.getPulseRate() : "--") + " bpm | Temp: " +
                    (enc.getTemperature() != null ? enc.getTemperature() + "°F" : "--") + " | SpO2: " +
                    (enc.getSpo2() != null ? enc.getSpo2() + "%" : "--") + " | BMI: " + (enc.getBmi() != null ? enc.getBmi() : "--"));
        }
        if (historyNotesLabel != null) {
            historyNotesLabel.setText("Chief Complaint: " + (enc.getChiefComplaint() != null ? enc.getChiefComplaint() : "N/A") + "\n" +
                    "Diagnosis: " + (enc.getDiagnosis() != null ? enc.getDiagnosis() : "N/A") + " (" + (enc.getIcdCode() != null ? enc.getIcdCode() : "") + ")\n" +
                    "Assessment Notes: " + (enc.getClinicalAssessment() != null ? enc.getClinicalAssessment() : "None"));
        }

        // Load Prescriptions for this visit
        if (historyPrescriptionListView != null) {
            List<PrescriptionEntity> rxs = prescriptionRepository.findByVisitId(enc.getVisitId());
            selectedHistoryPrescriptionItems.clear();
            List<String> rxStrings = new ArrayList<>();
            for (PrescriptionEntity rx : rxs) {
                if (rx.getItems() != null) {
                    for (PrescriptionItemEntity item : rx.getItems()) {
                        selectedHistoryPrescriptionItems.add(item);
                        rxStrings.add(item.getMedicineName() + " (Dose: " + item.getDose() + ", Duration: " + item.getDuration() + ")");
                    }
                }
            }
            historyPrescriptionListView.setItems(FXCollections.observableArrayList(rxStrings));
        }

        // Load Lab Orders for this visit
        if (historyLabListView != null) {
            List<LabOrderEntity> labs = labOrderRepository.findByVisitId(enc.getVisitId());
            List<String> labStrings = labs.stream()
                    .map(l -> l.getTestName() + " - Status: " + l.getStatus() + (l.getResult() != null ? " (Result: " + l.getResult() + ")" : ""))
                    .collect(Collectors.toList());
            historyLabListView.setItems(FXCollections.observableArrayList(labStrings));
        }
    }

    @FXML
    public void handleCopyHistoryPrescription() {
        if (selectedHistoryPrescriptionItems.isEmpty()) return;
        for (PrescriptionItemEntity item : selectedHistoryPrescriptionItems) {
            PrescriptionItemEntity newItem = new PrescriptionItemEntity();
            newItem.setMedicineName(item.getMedicineName());
            newItem.setDose(item.getDose());
            newItem.setDuration(item.getDuration());
            newItem.setInstructions(item.getInstructions());
            tempPrescriptionItems.add(newItem);

            if (prescriptionListView != null) {
                prescriptionListView.getItems().add(newItem.getMedicineName() + " (" + newItem.getDose() + ", " + newItem.getDuration() + ")");
            }
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Copied " + selectedHistoryPrescriptionItems.size() + " medication(s) to active prescription list.", ButtonType.OK);
        alert.show();
    }

    private void setupDoctorVisitsTable() {
        if (doctorVisitsTable == null) return;
        docVisitIdCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getVisitId()));
        docVisitDateCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getVisitDate()));
        docPatientCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPatientName()));
        docDiagnosisCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDiagnosis()));
        docStatusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));
    }

    private void loadDoctorVisits() {
        if (doctorVisitsTable == null) return;
        String selectedDoctor = doctorFilterCombo != null ? doctorFilterCombo.getValue() : "All Doctors";
        List<VisitEntity> allVisits = visitService.getAllVisits();

        if (selectedDoctor != null && !"All Doctors".equals(selectedDoctor)) {
            allVisits = allVisits.stream()
                    .filter(v -> selectedDoctor.equalsIgnoreCase(v.getDoctorName()))
                    .collect(Collectors.toList());
        }

        Map<String, PatientEntity> patientCache = patientService.getAllPatients().stream()
                .collect(Collectors.toMap(PatientEntity::getPatientId, p -> p, (p1, p2) -> p1));
        Map<String, ClinicalEncounterEntity> encounterMap = encounterRepository.findAll().stream()
                .collect(Collectors.toMap(ClinicalEncounterEntity::getVisitId, e -> e, (e1, e2) -> e1));

        List<DoctorVisitRow> rows = allVisits.stream().map(v -> {
            PatientEntity p = patientCache.get(v.getPatientId());
            String pName = (p != null) ? (p.getFirstName() + " " + p.getLastName() + " (" + v.getPatientId() + ")") : v.getPatientId();
            ClinicalEncounterEntity enc = encounterMap.get(v.getVisitId());
            String diag = (enc != null && enc.getDiagnosis() != null) ? enc.getDiagnosis() : "Pending Consultation";
            String dateStr = v.getVisitDate() != null ? v.getVisitDate().toString() : "N/A";
            return new DoctorVisitRow(v.getVisitId(), dateStr, pName, diag, v.getStatus());
        }).collect(Collectors.toList());

        doctorVisitsTable.setItems(FXCollections.observableArrayList(rows));
    }

    @FXML
    public void handleEditSelectedEncounter() {
        if (doctorVisitsTable == null) return;
        DoctorVisitRow selectedRow = doctorVisitsTable.getSelectionModel().getSelectedItem();
        if (selectedRow == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a visit from the table to edit.", ButtonType.OK);
            alert.show();
            return;
        }

        visitService.getVisitById(selectedRow.getVisitId()).ifPresent(v -> {
            this.activeVisit = v;
            loadPatientDetails(v.getPatientId());
            encounterRepository.findByVisitId(v.getVisitId()).ifPresent(this::populateEncounterToForm);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Visit record " + v.getVisitId() + " loaded into Active Consultation workspace for editing.", ButtonType.OK);
            alert.show();
        });
    }

    private void populateEncounterToForm(ClinicalEncounterEntity enc) {
        if (chiefComplaintField != null) chiefComplaintField.setText(enc.getChiefComplaint() != null ? enc.getChiefComplaint() : "");
        if (systolicBpField != null) systolicBpField.setText(enc.getSystolicBp() != null ? enc.getSystolicBp().toString() : "");
        if (diastolicBpField != null) diastolicBpField.setText(enc.getDiastolicBp() != null ? enc.getDiastolicBp().toString() : "");
        if (pulseField != null) pulseField.setText(enc.getPulseRate() != null ? enc.getPulseRate().toString() : "");
        if (tempField != null) tempField.setText(enc.getTemperature() != null ? enc.getTemperature().toString() : "");
        if (spo2Field != null) spo2Field.setText(enc.getSpo2() != null ? enc.getSpo2().toString() : "");
        if (weightField != null) weightField.setText(enc.getWeightKg() != null ? enc.getWeightKg().toString() : "");
        if (heightField != null) heightField.setText(enc.getHeightCm() != null ? enc.getHeightCm().toString() : "");
        if (bmiLabel != null && enc.getBmi() != null) bmiLabel.setText(enc.getBmi().toString());

        if (historyArea != null) historyArea.setText(enc.getMedicalHistory() != null ? enc.getMedicalHistory() : "");
        if (examArea != null) examArea.setText(enc.getPhysicalExamination() != null ? enc.getPhysicalExamination() : "");
        if (assessmentArea != null) assessmentArea.setText(enc.getClinicalAssessment() != null ? enc.getClinicalAssessment() : "");

        if (icdCodeField != null) icdCodeField.setText(enc.getIcdCode() != null ? enc.getIcdCode() : "");
        if (icdDescField != null) icdDescField.setText(enc.getIcdDescription() != null ? enc.getIcdDescription() : "");
        if (diagnosisField != null) diagnosisField.setText(enc.getDiagnosis() != null ? enc.getDiagnosis() : "");
    }

    @FXML
    public void handleAddMedicine() {
        if (medNameField == null || medNameField.getText().isEmpty()) return;

        PrescriptionItemEntity item = new PrescriptionItemEntity();
        item.setMedicineName(medNameField.getText());
        item.setDose(medDosageField != null && !medDosageField.getText().isEmpty() ? medDosageField.getText() : "1-0-1");
        item.setDuration(medDurationField != null && !medDurationField.getText().isEmpty() ? medDurationField.getText() : "5 Days");
        tempPrescriptionItems.add(item);

        if (prescriptionListView != null) {
            prescriptionListView.getItems().add(item.getMedicineName() + " (" + item.getDose() + ", " + item.getDuration() + ")");
        }
        medNameField.clear();
    }

    @FXML
    public void handleAddLabOrder() {
        if (labTestNameField == null || labTestNameField.getText().isEmpty()) return;

        String testName = labTestNameField.getText();
        tempLabTests.add(testName);
        if (labOrderListView != null) {
            labOrderListView.getItems().add(testName);
        }
        labTestNameField.clear();
    }

    @FXML
    public void handleSubmitEncounter() {
        if (activeVisit == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "No active visit loaded. Please load a patient visit first.", ButtonType.OK);
            alert.show();
            return;
        }

        // 1. Record / Update Encounter
        ClinicalEncounterEntity enc = encounterRepository.findByVisitId(activeVisit.getVisitId())
                .orElseGet(ClinicalEncounterEntity::new);

        enc.setVisitId(activeVisit.getVisitId());
        enc.setPatientId(activeVisit.getPatientId());
        enc.setDoctorId("DOC-000001");
        enc.setChiefComplaint(chiefComplaintField != null ? chiefComplaintField.getText() : "");
        if (systolicBpField != null && !systolicBpField.getText().isEmpty()) enc.setSystolicBp(Integer.parseInt(systolicBpField.getText()));
        if (diastolicBpField != null && !diastolicBpField.getText().isEmpty()) enc.setDiastolicBp(Integer.parseInt(diastolicBpField.getText()));
        if (pulseField != null && !pulseField.getText().isEmpty()) enc.setPulseRate(Integer.parseInt(pulseField.getText()));
        if (tempField != null && !tempField.getText().isEmpty()) enc.setTemperature(new BigDecimal(tempField.getText()));
        if (spo2Field != null && !spo2Field.getText().isEmpty()) enc.setSpo2(Integer.parseInt(spo2Field.getText()));
        if (weightField != null && !weightField.getText().isEmpty()) enc.setWeightKg(new BigDecimal(weightField.getText()));
        if (heightField != null && !heightField.getText().isEmpty()) enc.setHeightCm(new BigDecimal(heightField.getText()));
        if (bmiLabel != null && !bmiLabel.getText().equals("--.--")) enc.setBmi(new BigDecimal(bmiLabel.getText()));

        if (historyArea != null) enc.setMedicalHistory(historyArea.getText());
        if (examArea != null) enc.setPhysicalExamination(examArea.getText());
        if (assessmentArea != null) enc.setClinicalAssessment(assessmentArea.getText());

        if (icdCodeField != null) enc.setIcdCode(icdCodeField.getText());
        if (icdDescField != null) enc.setIcdDescription(icdDescField.getText());
        enc.setDiagnosis(diagnosisField != null && !diagnosisField.getText().isEmpty() ? diagnosisField.getText() : "Clinical Diagnosis");

        consultationService.recordEncounter(enc);

        // 2. Prescription Order
        if (!tempPrescriptionItems.isEmpty()) {
            prescriptionService.createPrescription(activeVisit.getVisitId(), tempPrescriptionItems);
        }

        // 3. Lab Orders
        for (String test : tempLabTests) {
            labService.createLabOrder(activeVisit.getVisitId(), test);
        }

        // 4. Referral
        if (refSpecialtyField != null && !refSpecialtyField.getText().isEmpty()) {
            ReferralEntity ref = new ReferralEntity();
            ref.setPatientId(activeVisit.getPatientId());
            ref.setEncounterId(enc.getEncounterId() != null ? enc.getEncounterId() : "ENC-001");
            ref.setReferringDoctorId("DOC-000001");
            ref.setTargetSpecialty(refSpecialtyField.getText());
            if (refHospitalField != null) ref.setTargetHospital(refHospitalField.getText());
            if (refReasonField != null) ref.setReason(refReasonField.getText());
            referralService.createReferral(ref);
        }

        // Update Visit Status
        visitService.updateStatus(activeVisit.getVisitId(), "COMPLETED");

        clearForm();
        loadVisitQueue();
        loadDoctorVisits();

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Consultation encounter saved successfully!", ButtonType.OK);
        alert.show();
    }

    private void clearForm() {
        this.activeVisit = null;
        this.activePatient = null;
        this.tempPrescriptionItems.clear();
        this.tempLabTests.clear();
        this.selectedHistoryPrescriptionItems.clear();
        if (patientSummaryLabel != null) patientSummaryLabel.setText("No Patient Loaded");
        if (allergiesAlertLabel != null) {
            allergiesAlertLabel.setText("⚠️ Allergies: None Recorded");
            allergiesAlertLabel.setStyle("-fx-background-color: #FEF2F2; -fx-text-fill: #DC2626; -fx-padding: 4 10 4 10; -fx-background-radius: 4; -fx-font-weight: bold;");
        }
        if (chiefComplaintField != null) chiefComplaintField.clear();
        if (systolicBpField != null) systolicBpField.clear();
        if (diastolicBpField != null) diastolicBpField.clear();
        if (pulseField != null) pulseField.clear();
        if (tempField != null) tempField.clear();
        if (spo2Field != null) spo2Field.clear();
        if (weightField != null) weightField.clear();
        if (heightField != null) heightField.clear();
        if (bmiLabel != null) bmiLabel.setText("--.--");
        if (historyArea != null) historyArea.clear();
        if (examArea != null) examArea.clear();
        if (assessmentArea != null) assessmentArea.clear();
        if (icdCodeField != null) icdCodeField.clear();
        if (icdDescField != null) icdDescField.clear();
        if (diagnosisField != null) diagnosisField.clear();
        if (prescriptionListView != null) prescriptionListView.getItems().clear();
        if (labOrderListView != null) labOrderListView.getItems().clear();
    }

    public static class DoctorVisitRow {
        private final String visitId;
        private final String visitDate;
        private final String patientName;
        private final String diagnosis;
        private final String status;

        public DoctorVisitRow(String visitId, String visitDate, String patientName, String diagnosis, String status) {
            this.visitId = visitId;
            this.visitDate = visitDate;
            this.patientName = patientName;
            this.diagnosis = diagnosis;
            this.status = status;
        }

        public String getVisitId() { return visitId; }
        public String getVisitDate() { return visitDate; }
        public String getPatientName() { return patientName; }
        public String getDiagnosis() { return diagnosis; }
        public String getStatus() { return status; }
    }
}

