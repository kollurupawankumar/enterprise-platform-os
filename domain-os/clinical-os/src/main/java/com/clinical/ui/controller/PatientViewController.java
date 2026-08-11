package com.clinical.ui.controller;

import com.clinical.patient.entity.PatientEntity;
import com.clinical.patient.service.PatientService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;

@Component
public class PatientViewController {

    private final PatientService patientService;

    // Tab 1 Fields
    @FXML private ComboBox<String> patientTypeCombo;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private ComboBox<String> genderCombo;
    @FXML private DatePicker dobPicker;
    @FXML private Label ageLabel;

    @FXML private TextField phoneField;
    @FXML private TextField altPhoneField;
    @FXML private TextField emailField;
    @FXML private TextField address1Field;
    @FXML private TextField cityField;
    @FXML private TextField stateField;
    @FXML private TextField pincodeField;

    @FXML private ComboBox<String> identityTypeCombo;
    @FXML private TextField identityNumberField;

    @FXML private ComboBox<String> maritalCombo;
    @FXML private TextField occupationField;
    @FXML private ComboBox<String> languageCombo;

    @FXML private TextField emergNameField;
    @FXML private ComboBox<String> emergRelCombo;
    @FXML private TextField emergPhoneField;

    @FXML private ComboBox<String> allergiesStatusCombo;
    @FXML private TextField allergiesListField;
    @FXML private TextField reportedAlertsField;

    @FXML private TitledPane guardianPane;
    @FXML private TextField guardianNameField;
    @FXML private ComboBox<String> guardianRelCombo;
    @FXML private TextField guardianPhoneField;

    @FXML private ComboBox<String> paymentCategoryCombo;
    @FXML private TextField insuranceProviderField;

    // Tab 2 Directory & Search
    @FXML private TextField searchField;
    @FXML private TableView<PatientEntity> patientTable;
    @FXML private TableColumn<PatientEntity, String> idCol;
    @FXML private TableColumn<PatientEntity, String> nameCol;
    @FXML private TableColumn<PatientEntity, String> genderCol;
    @FXML private TableColumn<PatientEntity, Integer> ageCol;
    @FXML private TableColumn<PatientEntity, String> phoneCol;
    @FXML private TableColumn<PatientEntity, String> categoryCol;

    public PatientViewController(PatientService patientService) {
        this.patientService = patientService;
    }

    @FXML
    public void initialize() {
        setupComboBoxes();
        setupDobListener();
        setupTableColumns();
        loadPatients();
    }

    private void setupComboBoxes() {
        if (patientTypeCombo != null) patientTypeCombo.setItems(FXCollections.observableArrayList("NEW", "EXISTING"));
        if (genderCombo != null) genderCombo.setItems(FXCollections.observableArrayList("Male", "Female", "Other"));
        if (identityTypeCombo != null) identityTypeCombo.setItems(FXCollections.observableArrayList("Aadhaar", "PAN", "Passport", "Driving License", "Other"));
        if (maritalCombo != null) maritalCombo.setItems(FXCollections.observableArrayList("Single", "Married", "Divorced", "Widowed"));
        if (languageCombo != null) languageCombo.setItems(FXCollections.observableArrayList("English", "Hindi", "Regional"));
        if (emergRelCombo != null) emergRelCombo.setItems(FXCollections.observableArrayList("Spouse", "Parent", "Child", "Friend", "Other"));
        if (allergiesStatusCombo != null) allergiesStatusCombo.setItems(FXCollections.observableArrayList("YES", "NO", "UNKNOWN"));
        if (guardianRelCombo != null) guardianRelCombo.setItems(FXCollections.observableArrayList("Parent", "Legal Guardian", "Caregiver", "Other"));
        if (paymentCategoryCombo != null) paymentCategoryCombo.setItems(FXCollections.observableArrayList("SELF_PAY", "INSURANCE", "CORPORATE", "GOVT_SCHEME"));

        if (patientTypeCombo != null) patientTypeCombo.getSelectionModel().select("NEW");
        if (genderCombo != null) genderCombo.getSelectionModel().select("Male");
        if (knownAllergiesComboHasDefault()) allergiesStatusCombo.getSelectionModel().select("UNKNOWN");
    }

    private boolean knownAllergiesComboHasDefault() {
        return allergiesStatusCombo != null;
    }

    private void setupDobListener() {
        if (dobPicker != null) {
            dobPicker.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    int age = Period.between(newVal, LocalDate.now()).getYears();
                    if (ageLabel != null) ageLabel.setText("Age: " + age);
                    if (guardianPane != null) {
                        guardianPane.setExpanded(age < 18);
                    }
                }
            });
        }
    }

    private void setupTableColumns() {
        if (idCol != null) {
            idCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPatientId()));
            nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFirstName() + " " + data.getValue().getLastName()));
            genderCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getGender()));
            ageCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getCalculatedAge()));
            phoneCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPhone()));
            categoryCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPaymentCategory()));
        }
    }

    @FXML
    public void handleRegister() {
        if (firstNameField == null || firstNameField.getText().isEmpty() || phoneField == null || phoneField.getText().isEmpty()) return;

        PatientEntity p = new PatientEntity();
        p.setPatientType(patientTypeCombo != null ? patientTypeCombo.getValue() : "NEW");
        p.setFirstName(firstNameField.getText());
        p.setLastName(lastNameField != null ? lastNameField.getText() : "");
        p.setGender(genderCombo != null ? genderCombo.getValue() : "Male");
        if (dobPicker != null) p.setDob(dobPicker.getValue());
        p.setPhone(phoneField.getText());
        if (altPhoneField != null) p.setAlternatePhone(altPhoneField.getText());
        if (emailField != null) p.setEmail(emailField.getText());
        if (address1Field != null) p.setAddress(address1Field.getText());
        if (cityField != null) p.setCity(cityField.getText());
        if (stateField != null) p.setState(stateField.getText());
        if (pincodeField != null) p.setPincode(pincodeField.getText());

        if (identityTypeCombo != null) p.setIdentityType(identityTypeCombo.getValue());
        if (identityNumberField != null) p.setIdentityNumber(identityNumberField.getText());

        if (maritalCombo != null) p.setMaritalStatus(maritalCombo.getValue());
        if (occupationField != null) p.setOccupation(occupationField.getText());
        if (languageCombo != null) p.setPreferredLanguage(languageCombo.getValue());

        if (emergNameField != null) p.setEmergencyContactName(emergNameField.getText());
        if (emergRelCombo != null) p.setEmergencyRelationship(emergRelCombo.getValue());
        if (emergPhoneField != null) p.setEmergencyContactPhone(emergPhoneField.getText());

        if (allergiesStatusCombo != null) p.setKnownAllergiesStatus(allergiesStatusCombo.getValue());
        if (allergiesListField != null) p.setAllergies(allergiesListField.getText());
        if (reportedAlertsField != null) p.setPatientReportedAlerts(reportedAlertsField.getText());

        if (guardianNameField != null) p.setGuardianName(guardianNameField.getText());
        if (guardianRelCombo != null) p.setGuardianRelationship(guardianRelCombo.getValue());
        if (guardianPhoneField != null) p.setGuardianPhone(guardianPhoneField.getText());

        if (paymentCategoryCombo != null) p.setPaymentCategory(paymentCategoryCombo.getValue());
        if (insuranceProviderField != null) p.setInsuranceProvider(insuranceProviderField.getText());

        patientService.registerPatient(p);
        loadPatients();
        handleClearForm();
    }

    @FXML
    public void handleClearForm() {
        if (firstNameField != null) firstNameField.clear();
        if (lastNameField != null) lastNameField.clear();
        if (phoneField != null) phoneField.clear();
        if (altPhoneField != null) altPhoneField.clear();
        if (emailField != null) emailField.clear();
        if (address1Field != null) address1Field.clear();
        if (cityField != null) cityField.clear();
        if (stateField != null) stateField.clear();
        if (pincodeField != null) pincodeField.clear();
        if (identityNumberField != null) identityNumberField.clear();
        if (occupationField != null) occupationField.clear();
        if (emergNameField != null) emergNameField.clear();
        if (emergPhoneField != null) emergPhoneField.clear();
        if (allergiesListField != null) allergiesListField.clear();
        if (reportedAlertsField != null) reportedAlertsField.clear();
        if (guardianNameField != null) guardianNameField.clear();
        if (guardianPhoneField != null) guardianPhoneField.clear();
        if (insuranceProviderField != null) insuranceProviderField.clear();
        if (dobPicker != null) dobPicker.setValue(null);
        if (ageLabel != null) ageLabel.setText("Age: --");
    }

    @FXML
    public void handleSearch() {
        String query = searchField != null ? searchField.getText() : "";
        if (query == null || query.trim().isEmpty()) {
            loadPatients();
        } else {
            patientTable.setItems(FXCollections.observableArrayList(
                    patientService.getAllPatients().stream()
                            .filter(p -> p.getPatientId().toLowerCase().contains(query.toLowerCase()) ||
                                         p.getPhone().contains(query) ||
                                         (p.getFirstName() + " " + p.getLastName()).toLowerCase().contains(query.toLowerCase()))
                            .toList()
            ));
        }
    }

    private void loadPatients() {
        if (patientTable != null) {
            patientTable.setItems(FXCollections.observableArrayList(patientService.getAllPatients()));
        }
    }
}
