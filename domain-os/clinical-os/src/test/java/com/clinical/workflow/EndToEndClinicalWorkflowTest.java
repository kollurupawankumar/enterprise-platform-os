package com.clinical.workflow;

import com.clinical.billing.entity.InvoiceEntity;
import com.clinical.billing.service.BillingService;
import com.clinical.doctor.entity.ClinicalEncounterEntity;
import com.clinical.doctor.service.DoctorConsultationService;
import com.clinical.lab.entity.LabOrderEntity;
import com.clinical.lab.service.LabDiagnosticsService;
import com.clinical.patient.entity.PatientEntity;
import com.clinical.patient.service.PatientService;
import com.clinical.pharmacy.entity.MedicineInventoryEntity;
import com.clinical.pharmacy.service.PharmacyService;
import com.clinical.prescription.entity.PrescriptionEntity;
import com.clinical.prescription.entity.PrescriptionItemEntity;
import com.clinical.visit.entity.VisitEntity;
import com.clinical.visit.service.VisitService;
import com.clinical.workflow.service.PatientCheckoutWorkflowService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EndToEndClinicalWorkflowTest {

    @Autowired
    private PatientService patientService;

    @Autowired
    private VisitService visitService;

    @Autowired
    private DoctorConsultationService doctorConsultationService;

    @Autowired
    private LabDiagnosticsService labDiagnosticsService;

    @Autowired
    private PharmacyService pharmacyService;

    @Autowired
    private PatientCheckoutWorkflowService checkoutWorkflowService;

    @Test
    void testCompletePatientJourneyWorkflow() {
        // 1. Patient Registration
        PatientEntity patient = new PatientEntity();
        patient.setFirstName("Ravi");
        patient.setLastName("Kumar");
        patient.setPhone("9876543210");
        patient.setGender("Male");
        patient.setDob(LocalDate.of(1990, 5, 15));
        PatientEntity savedPatient = patientService.registerPatient(patient);
        assertNotNull(savedPatient.getPatientId());

        // 2. Create Visit / OPD Queue
        VisitEntity visit = visitService.createVisit(savedPatient.getPatientId(), "Dr. Suresh", "OPD");
        assertNotNull(visit.getVisitId());
        assertEquals("WAITING", visit.getStatus());

        // 3. Doctor Consultation & Vitals
        ClinicalEncounterEntity encounter = new ClinicalEncounterEntity();
        encounter.setVisitId(visit.getVisitId());
        encounter.setChiefComplaint("Fever, Cough");
        encounter.setSystolicBp(120);
        encounter.setDiastolicBp(80);
        encounter.setTemperature(BigDecimal.valueOf(99.5));
        encounter.setDiagnosis("Viral Fever");
        ClinicalEncounterEntity recordedEncounter = doctorConsultationService.recordEncounter(encounter);
        assertNotNull(recordedEncounter.getId());

        // 4. Lab Diagnostic Orders
        LabOrderEntity labOrder = labDiagnosticsService.createLabOrder(visit.getVisitId(), "CBC");
        assertEquals("ORDERED", labOrder.getStatus());
        labDiagnosticsService.updateLabStatus(labOrder.getOrderId(), "COMPLETED", "Hb: 14.5 g/dL");

        // 5. Pharmacy Inventory & Stock Deduction
        MedicineInventoryEntity paracetamol = new MedicineInventoryEntity();
        paracetamol.setMedicineName("Paracetamol 500mg");
        paracetamol.setBatchNumber("BATCH-001");
        paracetamol.setQuantity(100);
        paracetamol.setSellingPrice(BigDecimal.valueOf(20));
        pharmacyService.addOrUpdateStock(paracetamol);

        PrescriptionEntity prescription = new PrescriptionEntity();
        prescription.setPrescriptionId("RX-0001");
        prescription.setVisitId(visit.getVisitId());
        PrescriptionItemEntity item = new PrescriptionItemEntity();
        item.setPrescriptionId("RX-0001");
        item.setMedicineName("Paracetamol 500mg");
        item.setDose("1 tablet");
        item.setFrequency("TID");
        item.setDuration("5 days");
        prescription.getItems().add(item);
        
        boolean dispensed = pharmacyService.dispensePrescription(prescription);
        assertTrue(dispensed);

        // 6. Patient Checkout & Consolidated Billing
        InvoiceEntity invoice = checkoutWorkflowService.processPatientCheckout(
                visit.getVisitId(),
                "UPI",
                BigDecimal.valueOf(500),
                BigDecimal.valueOf(300),
                BigDecimal.valueOf(100)
        );

        assertNotNull(invoice.getInvoiceId());
        assertEquals("PAID", invoice.getPaymentStatus());
        assertEquals(BigDecimal.valueOf(900), invoice.getTotalAmount());
    }
}
