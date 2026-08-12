package com.clinical.report.service;

import com.clinical.billing.entity.InvoiceEntity;
import com.clinical.doctor.entity.ClinicalEncounterEntity;
import com.clinical.doctor.entity.DoctorEntity;
import com.clinical.doctor.entity.ReferralEntity;
import com.clinical.lab.entity.LabOrderEntity;
import com.clinical.patient.entity.PatientEntity;
import com.clinical.pharmacy.entity.MedicineInventoryEntity;
import com.clinical.prescription.entity.PrescriptionEntity;
import com.clinical.prescription.entity.PrescriptionItemEntity;
import javafx.print.PrinterJob;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportPrintingService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm");

    // 1. Patient EHR Summary HTML Report
    public String generatePatientEhrReportHtml(PatientEntity patient, List<ClinicalEncounterEntity> encounters) {
        StringBuilder html = new StringBuilder();
        appendReportHeader(html, "PATIENT MEDICAL HISTORY & EHR SUMMARY");

        html.append("<div class='section'>")
            .append("<h3>Patient Demographics</h3>")
            .append("<p><b>Patient ID:</b> ").append(patient.getPatientId()).append(" &nbsp;&nbsp;&nbsp; <b>Name:</b> ").append(patient.getFirstName()).append(" ").append(patient.getLastName()).append("</p>")
            .append("<p><b>Gender:</b> ").append(patient.getGender()).append(" &nbsp;&nbsp;&nbsp; <b>DOB:</b> ").append(patient.getDob()).append(" &nbsp;&nbsp;&nbsp; <b>Mobile:</b> ").append(patient.getPhone()).append("</p>")
            .append("<p><b>Address:</b> ").append(patient.getAddress() != null ? patient.getAddress() : "N/A").append("</p>")
            .append("<p><b>Allergies:</b> <span class='alert'>").append(patient.getAllergies() != null ? patient.getAllergies() : "None Recorded").append("</span></p>")
            .append("</div>");

        html.append("<div class='section'><h3>Clinical Encounters & Consultation History</h3>");
        if (encounters == null || encounters.isEmpty()) {
            html.append("<p>No past clinical encounters recorded.</p>");
        } else {
            html.append("<table><thead><tr><th>Date</th><th>Encounter ID</th><th>Chief Complaint</th><th>Vitals (BP / Pulse / Temp)</th><th>Diagnosis (ICD-10)</th></tr></thead><tbody>");
            for (ClinicalEncounterEntity enc : encounters) {
                html.append("<tr>")
                    .append("<td>").append(enc.getCreatedAt() != null ? enc.getCreatedAt().format(DATE_FORMATTER) : "N/A").append("</td>")
                    .append("<td>").append(enc.getEncounterId()).append("</td>")
                    .append("<td>").append(enc.getChiefComplaint() != null ? enc.getChiefComplaint() : "").append("</td>")
                    .append("<td>").append(enc.getSystolicBp() != null ? enc.getSystolicBp() + "/" + enc.getDiastolicBp() : "--").append(" mmHg | ")
                    .append(enc.getPulseRate() != null ? enc.getPulseRate() : "--").append(" bpm | ")
                    .append(enc.getTemperature() != null ? enc.getTemperature() + "°F" : "--").append("</td>")
                    .append("<td><b>").append(enc.getDiagnosis() != null ? enc.getDiagnosis() : "").append("</b> (").append(enc.getIcdCode() != null ? enc.getIcdCode() : "--").append(")</td>")
                    .append("</tr>");
            }
            html.append("</tbody></table>");
        }
        html.append("</div>");

        appendReportFooter(html);
        return html.toString();
    }

    // 2. Official Bill Invoice & Receipt HTML Report
    public String generateInvoiceReceiptHtml(InvoiceEntity invoice, PatientEntity patient) {
        StringBuilder html = new StringBuilder();
        appendReportHeader(html, "OFFICIAL BILL INVOICE & PAYMENT RECEIPT");

        html.append("<div class='section'>")
            .append("<p><b>Invoice No:</b> ").append(invoice.getInvoiceId()).append(" &nbsp;&nbsp;&nbsp;&nbsp; <b>Date:</b> ").append(invoice.getCreatedAt() != null ? invoice.getCreatedAt().format(DATE_FORMATTER) : LocalDateTime.now().format(DATE_FORMATTER)).append("</p>")
            .append("<p><b>Patient Name:</b> ").append(patient != null ? patient.getFirstName() + " " + patient.getLastName() : invoice.getPatientId()).append(" (ID: ").append(invoice.getPatientId()).append(")</p>")
            .append("<p><b>Payment Status:</b> <span class='badge ").append("PAID".equalsIgnoreCase(invoice.getPaymentStatus()) ? "paid" : "unpaid").append("'>").append(invoice.getPaymentStatus()).append("</span> &nbsp;&nbsp; <b>Payment Mode:</b> ").append(invoice.getPaymentMode() != null ? invoice.getPaymentMode() : "N/A").append("</p>")
            .append("</div>");

        html.append("<div class='section'><table><thead><tr><th>Description</th><th style='text-align:right;'>Amount (₹)</th></tr></thead><tbody>")
            .append("<tr><td>Doctor Consultation Fee</td><td style='text-align:right;'>").append(invoice.getConsultationFee()).append("</td></tr>")
            .append("<tr><td>Laboratory Diagnostics Fee</td><td style='text-align:right;'>").append(invoice.getLabFee()).append("</td></tr>")
            .append("<tr><td>Pharmacy Medicines Dispensed</td><td style='text-align:right;'>").append(invoice.getPharmacyFee()).append("</td></tr>")
            .append("<tr style='font-weight:bold; background-color:#F1F5F9;'><td>Total Invoice Amount</td><td style='text-align:right;'>₹ ").append(invoice.getTotalAmount()).append("</td></tr>")
            .append("</tbody></table></div>");

        appendReportFooter(html);
        return html.toString();
    }

    // 3. Official Laboratory Test Report HTML
    public String generateLabReportHtml(LabOrderEntity labOrder, PatientEntity patient) {
        StringBuilder html = new StringBuilder();
        appendReportHeader(html, "LABORATORY DIAGNOSTIC TEST REPORT");

        html.append("<div class='section'>")
            .append("<p><b>Order ID:</b> ").append(labOrder.getOrderId()).append(" &nbsp;&nbsp;&nbsp;&nbsp; <b>Date:</b> ").append(labOrder.getCreatedAt() != null ? labOrder.getCreatedAt().format(DATE_FORMATTER) : "N/A").append("</p>")
            .append("<p><b>Patient Name:</b> ").append(patient != null ? patient.getFirstName() + " " + patient.getLastName() : labOrder.getPatientId()).append(" &nbsp;&nbsp;&nbsp;&nbsp; <b>Test Name:</b> ").append(labOrder.getTestName()).append("</p>")
            .append("<p><b>Test Status:</b> <span class='badge paid'>").append(labOrder.getStatus()).append("</span></p>")
            .append("</div>");

        html.append("<div class='section'><h3>Observed Findings & Result Parameters</h3>")
            .append("<div style='background-color:#F8FAFC; padding:15px; border-radius:6px; border:1px solid #CBD5E1;'>")
            .append("<p><b>Result Values:</b></p><p style='font-family:monospace; font-size:14px;'>").append(labOrder.getResult() != null ? labOrder.getResult() : "Awaiting lab analysis...").append("</p>")
            .append("<br/><p><b>Pathologist / Technician Remarks:</b> ").append(labOrder.getRemarks() != null ? labOrder.getRemarks() : "None").append("</p>")
            .append("</div></div>");

        appendReportFooter(html);
        return html.toString();
    }

    public String generateLabReportHtml(LabOrderEntity labOrder) {
        return generateLabReportHtml(labOrder, null);
    }

    // 4. Doctor Schedule & Performance Report HTML
    public String generateDoctorRosterReportHtml(List<DoctorEntity> doctors) {
        StringBuilder html = new StringBuilder();
        appendReportHeader(html, "DOCTOR ROSTER & SPECIALTY DIRECTORY");

        html.append("<div class='section'><table><thead><tr><th>Doctor ID</th><th>Name</th><th>Specialty</th><th>License No</th><th>Consultation Fee</th><th>Status</th></tr></thead><tbody>");
        for (DoctorEntity doc : doctors) {
            html.append("<tr>")
                .append("<td>").append(doc.getDoctorId()).append("</td>")
                .append("<td><b>").append(doc.getDisplayName() != null ? doc.getDisplayName() : doc.getName()).append("</b></td>")
                .append("<td>").append(doc.getSpecialization()).append("</td>")
                .append("<td>").append(doc.getLicenseNumber()).append("</td>")
                .append("<td>₹ ").append(doc.getConsultationFee()).append("</td>")
                .append("<td><span class='badge paid'>").append(doc.getLifecycleStatus() != null ? doc.getLifecycleStatus() : "ACTIVE").append("</span></td>")
                .append("</tr>");
        }
        html.append("</tbody></table></div>");

        appendReportFooter(html);
        return html.toString();
    }

    // 5. Pharmacy Inventory Stock & Low Stock Audit Report HTML
    public String generatePharmacyStockReportHtml(List<MedicineInventoryEntity> medicines) {
        StringBuilder html = new StringBuilder();
        appendReportHeader(html, "PHARMACY INVENTORY & STOCK AUDIT REPORT");

        html.append("<div class='section'><table><thead><tr><th>Medicine Name</th><th>Batch No</th><th>Expiry Date</th><th>Current Stock</th><th>Reorder Level</th><th>Unit Selling Price</th><th>Status</th></tr></thead><tbody>");
        for (MedicineInventoryEntity med : medicines) {
            boolean isLow = med.getQuantity() != null && med.getReorderLevel() != null && med.getQuantity() <= med.getReorderLevel();
            html.append("<tr>")
                .append("<td><b>").append(med.getMedicineName()).append("</b></td>")
                .append("<td>").append(med.getBatchNumber()).append("</td>")
                .append("<td>").append(med.getExpiryDate()).append("</td>")
                .append("<td>").append(med.getQuantity()).append("</td>")
                .append("<td>").append(med.getReorderLevel()).append("</td>")
                .append("<td>₹ ").append(med.getSellingPrice()).append("</td>")
                .append("<td>").append(isLow ? "<span class='badge unpaid'>LOW STOCK</span>" : "<span class='badge paid'>IN STOCK</span>").append("</td>")
                .append("</tr>");
        }
        html.append("</tbody></table></div>");

        appendReportFooter(html);
        return html.toString();
    }

    // 6. Discharge & Specialist Referral Summary Report HTML
    public String generateReferralSummaryHtml(ReferralEntity referral, PatientEntity patient) {
        StringBuilder html = new StringBuilder();
        appendReportHeader(html, "OFFICIAL SPECIALIST REFERRAL LETTER");

        html.append("<div class='section'>")
            .append("<p><b>Referral Ref No:</b> ").append(referral.getReferralId()).append(" &nbsp;&nbsp;&nbsp;&nbsp; <b>Urgency:</b> <span class='badge unpaid'>").append(referral.getUrgency()).append("</span></p>")
            .append("<p><b>Patient Name:</b> ").append(patient != null ? patient.getFirstName() + " " + patient.getLastName() : referral.getPatientId()).append(" (ID: ").append(referral.getPatientId()).append(")</p>")
            .append("<p><b>Referred To Specialty:</b> ").append(referral.getTargetSpecialty()).append(" &nbsp;&nbsp;&nbsp;&nbsp; <b>Hospital:</b> ").append(referral.getTargetHospital() != null ? referral.getTargetHospital() : "External Referral").append("</p>")
            .append("<div style='margin-top:15px; padding:12px; background-color:#F1F5F9; border-radius:6px;'>")
            .append("<p><b>Reason for Referral & Clinical Impression:</b></p><p>").append(referral.getReason()).append("</p>")
            .append("</div></div>");

        appendReportFooter(html);
        return html.toString();
    }

    // Native JavaFX Printing Execution
    public void printHtmlDocument(String htmlContent) {
        try {
            WebView webView = new WebView();
            WebEngine engine = webView.getEngine();
            engine.loadContent(htmlContent);

            engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                    PrinterJob job = PrinterJob.createPrinterJob();
                    if (job != null && job.showPrintDialog(null)) {
                        engine.print(job);
                        job.endJob();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void appendReportHeader(StringBuilder html, String title) {
        html.append("<!DOCTYPE html><html><head><style>")
            .append("body { font-family: 'Helvetica Neue', Arial, sans-serif; margin: 20px; color: #1E293B; }")
            .append(".header { text-align: center; border-bottom: 2px solid #0284C7; padding-bottom: 10px; margin-bottom: 20px; }")
            .append(".header h1 { margin: 0; color: #0284C7; font-size: 22px; }")
            .append(".header h2 { margin: 5px 0 0 0; color: #475569; font-size: 14px; text-transform: uppercase; letter-spacing: 1px; }")
            .append(".section { margin-bottom: 20px; }")
            .append("table { width: 100%; border-collapse: collapse; margin-top: 10px; }")
            .append("th, td { border: 1px solid #CBD5E1; padding: 8px 12px; text-align: left; font-size: 13px; }")
            .append("th { background-color: #F1F5F9; color: #334155; font-weight: bold; }")
            .append(".badge { padding: 3px 8px; border-radius: 4px; font-weight: bold; font-size: 11px; }")
            .append(".paid { background-color: #DCFCE7; color: #166534; }")
            .append(".unpaid { background-color: #FEE2E2; color: #991B1B; }")
            .append(".alert { color: #DC2626; font-weight: bold; }")
            .append(".footer { margin-top: 40px; border-top: 1px solid #E2E8F0; padding-top: 10px; font-size: 11px; color: #94A3B8; text-align: center; }")
            .append("</style></head><body>")
            .append("<div class='header'><h1>APEX MULTISPECIALTY CLINIC</h1><p>123 Healthcare Boulevard, Tech City | Reg No: REG-2026-CLINIC-88</p><h2>").append(title).append("</h2></div>");
    }

    private void appendReportFooter(StringBuilder html) {
        html.append("<div class='footer'>")
            .append("<p>ClinicalOS Enterprise Management Platform | Confidential Medical Document | Printed: ").append(LocalDateTime.now().format(DATE_FORMATTER)).append("</p>")
            .append("</div></body></html>");
    }
}
