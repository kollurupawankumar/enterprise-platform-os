package com.clinical.ui.controller;

import com.clinical.doctor.entity.ClinicalEncounterEntity;
import com.clinical.doctor.entity.ReferralEntity;
import com.clinical.doctor.service.DoctorConsultationService;
import com.clinical.doctor.service.ReferralService;
import com.clinical.lab.service.LabDiagnosticsService;
import com.clinical.prescription.entity.PrescriptionItemEntity;
import com.clinical.prescription.service.PrescriptionService;
import com.clinical.visit.entity.VisitEntity;
import com.clinical.visit.service.VisitService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class DoctorViewController {

    private final VisitService visitService;
    private final DoctorConsultationService consultationService;
    private final PrescriptionService prescriptionService;
    private final LabDiagnosticsService labService;
    private final ReferralService referralService;

    @FXML private ComboBox<String> visitQueueCombo;
    @FXML private Label patientSummaryLabel;

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

    private VisitEntity activeVisit;
    private final List<PrescriptionItemEntity> tempPrescriptionItems = new ArrayList<>();
    private final List<String> tempLabTests = new ArrayList<>();

    public DoctorViewController(VisitService visitService,
                               DoctorConsultationService consultationService,
                               PrescriptionService prescriptionService,
                               LabDiagnosticsService labService,
                               ReferralService referralService) {
        this.visitService = visitService;
        this.consultationService = consultationService;
        this.prescriptionService = prescriptionService;
        this.labService = labService;
        this.referralService = referralService;
    }

    @FXML
    public void initialize() {
        loadVisitQueue();
        setupBmiListener();
    }

    private void loadVisitQueue() {
        if (visitQueueCombo != null) {
            List<VisitEntity> activeVisits = visitService.getActiveVisits();
            List<String> displayList = activeVisits.stream()
                    .map(v -> v.getVisitId() + " - " + v.getPatientId() + " (Status: " + v.getStatus() + ")")
                    .toList();
            visitQueueCombo.setItems(FXCollections.observableArrayList(displayList));
        }
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
                    if (patientSummaryLabel != null) {
                        patientSummaryLabel.setText("Active Patient: " + v.getPatientId() + " | Doctor: " + v.getDoctorName());
                    }
                });
    }

    @FXML
    public void handleAddMedicine() {
        if (medNameField == null || medNameField.getText().isEmpty()) return;

        PrescriptionItemEntity item = new PrescriptionItemEntity();
        item.setMedicineName(medNameField.getText());
        item.setDose(medDosageField != null ? medDosageField.getText() : "1-0-1");
        item.setDuration(medDurationField != null ? medDurationField.getText() : "5 Days");
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
        if (activeVisit == null) return;

        // 1. Record Encounter
        ClinicalEncounterEntity enc = new ClinicalEncounterEntity();
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
        enc.setDiagnosis(diagnosisField != null ? diagnosisField.getText() : "Clinical Diagnosis");

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
    }

    private void clearForm() {
        this.activeVisit = null;
        this.tempPrescriptionItems.clear();
        this.tempLabTests.clear();
        if (patientSummaryLabel != null) patientSummaryLabel.setText("");
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
}
