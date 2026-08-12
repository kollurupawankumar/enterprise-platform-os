package com.clinical.ui.controller;

import com.clinical.billing.entity.InvoiceEntity;
import com.clinical.billing.repository.InvoiceRepository;
import com.clinical.doctor.entity.DoctorEntity;
import com.clinical.doctor.entity.ReferralEntity;
import com.clinical.doctor.repository.ClinicalEncounterRepository;
import com.clinical.doctor.repository.DoctorRepository;
import com.clinical.doctor.repository.ReferralRepository;
import com.clinical.lab.entity.LabOrderEntity;
import com.clinical.lab.repository.LabOrderRepository;
import com.clinical.patient.entity.PatientEntity;
import com.clinical.patient.repository.PatientRepository;
import com.clinical.pharmacy.entity.MedicineInventoryEntity;
import com.clinical.pharmacy.repository.MedicineInventoryRepository;
import com.clinical.report.service.ReportPrintingService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReportsViewController {

    private final ReportPrintingService printingService;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final InvoiceRepository invoiceRepository;
    private final LabOrderRepository labOrderRepository;
    private final MedicineInventoryRepository medicineRepository;
    private final ClinicalEncounterRepository encounterRepository;
    private final ReferralRepository referralRepository;

    @FXML private ComboBox<String> patientCombo;
    @FXML private ComboBox<String> invoiceCombo;
    @FXML private ComboBox<String> labOrderCombo;
    @FXML private ComboBox<String> referralCombo;

    public ReportsViewController(ReportPrintingService printingService,
                                PatientRepository patientRepository,
                                DoctorRepository doctorRepository,
                                InvoiceRepository invoiceRepository,
                                LabOrderRepository labOrderRepository,
                                MedicineInventoryRepository medicineRepository,
                                ClinicalEncounterRepository encounterRepository,
                                ReferralRepository referralRepository) {
        this.printingService = printingService;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.invoiceRepository = invoiceRepository;
        this.labOrderRepository = labOrderRepository;
        this.medicineRepository = medicineRepository;
        this.encounterRepository = encounterRepository;
        this.referralRepository = referralRepository;
    }

    @FXML
    public void initialize() {
        if (patientCombo != null) {
            List<String> patients = patientRepository.findAll().stream()
                    .map(p -> p.getPatientId() + " - " + p.getFirstName() + " " + p.getLastName())
                    .toList();
            patientCombo.setItems(FXCollections.observableArrayList(patients));
        }
        if (invoiceCombo != null) {
            List<String> invoices = invoiceRepository.findAll().stream()
                    .map(InvoiceEntity::getInvoiceId)
                    .toList();
            invoiceCombo.setItems(FXCollections.observableArrayList(invoices));
        }
        if (labOrderCombo != null) {
            List<String> labOrders = labOrderRepository.findAll().stream()
                    .map(l -> l.getOrderId() + " - " + l.getTestName())
                    .toList();
            labOrderCombo.setItems(FXCollections.observableArrayList(labOrders));
        }
        if (referralCombo != null) {
            List<String> referrals = referralRepository.findAll().stream()
                    .map(ReferralEntity::getReferralId)
                    .toList();
            referralCombo.setItems(FXCollections.observableArrayList(referrals));
        }
    }

    @FXML
    public void handlePrintPatientEhr() {
        String sel = patientCombo != null ? patientCombo.getValue() : null;
        if (sel == null) return;
        String patientId = sel.split(" - ")[0];
        patientRepository.findByPatientId(patientId).ifPresent(p -> {
            String html = printingService.generatePatientEhrReportHtml(p, encounterRepository.findAll());
            printingService.printHtmlDocument(html);
        });
    }

    @FXML
    public void handlePrintInvoice() {
        String invId = invoiceCombo != null ? invoiceCombo.getValue() : null;
        if (invId == null) return;
        invoiceRepository.findAll().stream().filter(i -> i.getInvoiceId().equals(invId)).findFirst().ifPresent(inv -> {
            PatientEntity patient = patientRepository.findByPatientId(inv.getPatientId()).orElse(null);
            String html = printingService.generateInvoiceReceiptHtml(inv, patient);
            printingService.printHtmlDocument(html);
        });
    }

    @FXML
    public void handlePrintLabReport() {
        String sel = labOrderCombo != null ? labOrderCombo.getValue() : null;
        if (sel == null) return;
        String orderId = sel.split(" - ")[0];
        labOrderRepository.findAll().stream().filter(l -> l.getOrderId().equals(orderId)).findFirst().ifPresent(lab -> {
            PatientEntity patient = patientRepository.findByPatientId(lab.getPatientId()).orElse(null);
            String html = printingService.generateLabReportHtml(lab, patient);
            printingService.printHtmlDocument(html);
        });
    }

    @FXML
    public void handlePrintDoctorRoster() {
        List<DoctorEntity> doctors = doctorRepository.findAll();
        String html = printingService.generateDoctorRosterReportHtml(doctors);
        printingService.printHtmlDocument(html);
    }

    @FXML
    public void handlePrintPharmacyStock() {
        List<MedicineInventoryEntity> stock = medicineRepository.findAll();
        String html = printingService.generatePharmacyStockReportHtml(stock);
        printingService.printHtmlDocument(html);
    }

    @FXML
    public void handlePrintReferral() {
        String refId = referralCombo != null ? referralCombo.getValue() : null;
        if (refId == null) return;
        referralRepository.findAll().stream().filter(r -> r.getReferralId().equals(refId)).findFirst().ifPresent(ref -> {
            PatientEntity patient = patientRepository.findByPatientId(ref.getPatientId()).orElse(null);
            String html = printingService.generateReferralSummaryHtml(ref, patient);
            printingService.printHtmlDocument(html);
        });
    }
}
