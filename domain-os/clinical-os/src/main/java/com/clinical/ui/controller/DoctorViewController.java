package com.clinical.ui.controller;

import com.clinical.doctor.entity.ClinicalEncounterEntity;
import com.clinical.doctor.service.DoctorConsultationService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;

@Component
public class DoctorViewController {

    private final DoctorConsultationService doctorConsultationService;

    @FXML private TextField visitIdField;
    @FXML private TextField bpField;
    @FXML private TextField tempField;
    @FXML private TextField spo2Field;
    @FXML private TextArea complaintsField;
    @FXML private TextField diagnosisField;
    @FXML private TextField icdField;

    public DoctorViewController(DoctorConsultationService doctorConsultationService) {
        this.doctorConsultationService = doctorConsultationService;
    }

    @FXML
    public void handleSaveEncounter() {
        if (visitIdField == null || visitIdField.getText().isEmpty()) return;
        ClinicalEncounterEntity encounter = new ClinicalEncounterEntity();
        encounter.setVisitId(visitIdField.getText());
        encounter.setBp(bpField.getText());
        encounter.setTemperature(tempField.getText());
        encounter.setSpo2(spo2Field.getText());
        encounter.setChiefComplaints(complaintsField.getText());
        encounter.setDiagnosis(diagnosisField.getText());
        encounter.setIcdCode(icdField.getText());
        doctorConsultationService.recordEncounter(encounter);
        clearFields();
    }

    private void clearFields() {
        if (visitIdField != null) visitIdField.clear();
        if (bpField != null) bpField.clear();
        if (tempField != null) tempField.clear();
        if (spo2Field != null) spo2Field.clear();
        if (complaintsField != null) complaintsField.clear();
        if (diagnosisField != null) diagnosisField.clear();
        if (icdField != null) icdField.clear();
    }
}
