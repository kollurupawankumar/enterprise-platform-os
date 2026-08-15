package com.clinical.report.service;

import com.clinical.admin.service.ClinicSettingService;
import com.clinical.billing.entity.InvoiceEntity;
import com.clinical.doctor.entity.ClinicalEncounterEntity;
import com.clinical.doctor.entity.DoctorEntity;
import com.clinical.doctor.entity.ReferralEntity;
import com.clinical.lab.entity.LabOrderEntity;
import com.clinical.patient.entity.PatientEntity;
import com.clinical.pharmacy.entity.MedicineInventoryEntity;
import com.clinical.prescription.entity.PrescriptionEntity;
import com.clinical.prescription.entity.PrescriptionItemEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportPrintingService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm");

    private final ClinicSettingService clinicSettingService;

    @Autowired
    public ReportPrintingService(ClinicSettingService clinicSettingService) {
        this.clinicSettingService = clinicSettingService;
    }

    public ReportPrintingService() {
        this.clinicSettingService = null;
    }

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

    // 3. Official Laboratory Diagnostic Test Certificate HTML
    public String generateLabReportHtml(LabOrderEntity labOrder, PatientEntity patient) {
        StringBuilder html = new StringBuilder();
        appendReportHeader(html, "OFFICIAL LABORATORY DIAGNOSTIC CERTIFICATE");

        String ageGender = (patient != null && patient.getCalculatedAge() != null) ? patient.getCalculatedAge() + " Yrs / " + patient.getGender() : "N/A";
        String pName = patient != null ? patient.getFirstName() + " " + patient.getLastName() : (labOrder.getPatientId() != null ? labOrder.getPatientId() : "Walk-in Patient");

        html.append("<div class='section'>")
            .append("<table style='border:none;'>")
            .append("<tr><td><b>Order ID:</b> ").append(labOrder.getOrderId()).append("</td><td><b>Date / Time:</b> ").append(labOrder.getCreatedAt() != null ? labOrder.getCreatedAt().format(DATE_FORMATTER) : "N/A").append("</td></tr>")
            .append("<tr><td><b>Patient Name:</b> ").append(pName).append(" (ID: ").append(labOrder.getPatientId() != null ? labOrder.getPatientId() : "N/A").append(")</td><td><b>Age / Gender:</b> ").append(ageGender).append("</td></tr>")
            .append("<tr><td><b>Referred By Doctor:</b> ").append(labOrder.getDoctorName() != null ? labOrder.getDoctorName() : "Self / Walk-in").append("</td><td><b>Test Status:</b> <span class='badge paid'>").append(labOrder.getStatus()).append("</span></td></tr>")
            .append("</table></div>");

        html.append("<div class='section'><h3>Diagnostic Findings & Parameter Results</h3>");

        if (labOrder.getItems() != null && !labOrder.getItems().isEmpty()) {
            html.append("<table><thead><tr><th>Test / Parameter Name</th><th>Observed Result</th><th>Units</th><th>Normal Reference Range</th><th>Flag Alert</th></tr></thead><tbody>");
            for (com.clinical.lab.entity.LabOrderItemEntity item : labOrder.getItems()) {
                String val = item.getResultValue() != null ? item.getResultValue() : "Pending";
                String flag = item.getFlag() != null ? item.getFlag() : "NORMAL";
                String flagBadge = "NORMAL".equalsIgnoreCase(flag) ? "<span class='badge paid'>NORMAL</span>" :
                        ("<span style='background-color:#FEF2F2; color:#DC2626; padding:3px 8px; border-radius:4px; font-weight:bold;'>⚠️ " + flag + "</span>");

                html.append("<tr>")
                    .append("<td><b>").append(item.getTestName()).append("</b> <small style='color:#64748B;'>(").append(item.getTestCode()).append(")</small></td>")
                    .append("<td style='font-weight:bold; font-size:14px;'>").append(val).append("</td>")
                    .append("<td>").append(item.getUnits() != null ? item.getUnits() : "--").append("</td>")
                    .append("<td>").append(item.getNormalRange() != null ? item.getNormalRange() : "--").append("</td>")
                    .append("<td>").append(flagBadge).append("</td>")
                    .append("</tr>");
            }
            html.append("</tbody></table>");
        } else {
            html.append("<div style='background-color:#F8FAFC; padding:15px; border-radius:6px; border:1px solid #CBD5E1;'>")
                .append("<p><b>Tests Requested:</b> ").append(labOrder.getTestName()).append("</p>")
                .append("<p><b>Result Findings:</b></p><p style='font-family:monospace;'>").append(labOrder.getResult() != null ? labOrder.getResult() : "Awaiting lab analysis...").append("</p>")
                .append("<p><b>Pathologist Remarks:</b> ").append(labOrder.getRemarks() != null ? labOrder.getRemarks() : "None").append("</p>")
                .append("</div>");
        }
        html.append("</div>");

        html.append("<br/><div style='margin-top:30px; text-align:right;'><p style='border-top:1px solid #94A3B8; display:inline-block; padding-top:5px; width:220px; text-align:center;'><b>Authorized Pathologist Signature</b></p></div>");

        appendReportFooter(html);
        return html.toString();
    }

    public String generateLabReportHtml(LabOrderEntity labOrder) {
        return generateLabReportHtml(labOrder, null);
    }

    // 3b. Standalone Laboratory Receipt HTML
    public String generateLabReceiptHtml(LabOrderEntity labOrder, PatientEntity patient) {
        StringBuilder html = new StringBuilder();
        appendReportHeader(html, "LABORATORY PAYMENT RECEIPT & INVOICE");

        String pName = patient != null ? patient.getFirstName() + " " + patient.getLastName() : (labOrder.getPatientId() != null ? labOrder.getPatientId() : "Walk-in Patient");

        html.append("<div class='section'>")
            .append("<p><b>Receipt Order No:</b> ").append(labOrder.getOrderId()).append(" &nbsp;&nbsp;&nbsp;&nbsp; <b>Date:</b> ").append(labOrder.getCreatedAt() != null ? labOrder.getCreatedAt().format(DATE_FORMATTER) : LocalDateTime.now().format(DATE_FORMATTER)).append("</p>")
            .append("<p><b>Patient Name:</b> ").append(pName).append(" &nbsp;&nbsp;&nbsp;&nbsp; <b>Doctor Name:</b> ").append(labOrder.getDoctorName() != null ? labOrder.getDoctorName() : "Self / Walk-in").append("</p>")
            .append("<p><b>Payment Status:</b> <span class='badge ").append("PAID".equalsIgnoreCase(labOrder.getPaymentStatus()) ? "paid" : "unpaid").append("'>").append(labOrder.getPaymentStatus()).append("</span> &nbsp;&nbsp; <b>Payment Mode:</b> ").append(labOrder.getPaymentMode() != null ? labOrder.getPaymentMode() : "CASH").append("</p>")
            .append("</div>");

        html.append("<div class='section'><h3>Diagnostic Test Itemization</h3><table><thead><tr><th>Test Code</th><th>Test Name</th><th style='text-align:right;'>Price (₹)</th></tr></thead><tbody>");

        if (labOrder.getItems() != null && !labOrder.getItems().isEmpty()) {
            for (com.clinical.lab.entity.LabOrderItemEntity item : labOrder.getItems()) {
                html.append("<tr><td>").append(item.getTestCode()).append("</td><td>").append(item.getTestName()).append("</td><td style='text-align:right;'>₹ ").append(item.getPrice()).append("</td></tr>");
            }
        } else {
            html.append("<tr><td>LAB-001</td><td>").append(labOrder.getTestName()).append("</td><td style='text-align:right;'>₹ ").append(labOrder.getTotalAmount() != null ? labOrder.getTotalAmount() : "0.00").append("</td></tr>");
        }

        html.append("<tr style='font-weight:bold; background-color:#F1F5F9;'><td colspan='2'>Total Amount Paid</td><td style='text-align:right;'>₹ ").append(labOrder.getTotalAmount() != null ? labOrder.getTotalAmount() : "0.00").append("</td></tr>");
        html.append("</tbody></table></div>");

        appendReportFooter(html);
        return html.toString();
    }

    // 3c. Official Pharmacy Invoice & Dispensing Advice HTML
    public String generatePharmacyInvoiceHtml(com.clinical.pharmacy.entity.PharmacyInvoiceEntity invoice, PatientEntity patient) {
        StringBuilder html = new StringBuilder();
        appendReportHeader(html, "PHARMACY DISPENSING INVOICE & RECEIPT");

        String pName = patient != null ? patient.getFirstName() + " " + patient.getLastName() : (invoice.getPatientId() != null ? invoice.getPatientId() : "Walk-in OTC Customer");

        html.append("<div class='section'>")
            .append("<p><b>Invoice No:</b> ").append(invoice.getInvoiceId()).append(" &nbsp;&nbsp;&nbsp;&nbsp; <b>Date / Time:</b> ").append(invoice.getCreatedAt() != null ? invoice.getCreatedAt().format(DATE_FORMATTER) : LocalDateTime.now().format(DATE_FORMATTER)).append("</p>")
            .append("<p><b>Patient Name:</b> ").append(pName).append(" &nbsp;&nbsp;&nbsp;&nbsp; <b>Prescribing Doctor:</b> ").append(invoice.getDoctorName() != null ? invoice.getDoctorName() : "Self / OTC").append("</p>")
            .append("<p><b>Payment Status:</b> <span class='badge ").append("PAID".equalsIgnoreCase(invoice.getPaymentStatus()) ? "paid" : "unpaid").append("'>").append(invoice.getPaymentStatus()).append("</span> &nbsp;&nbsp; <b>Payment Mode:</b> ").append(invoice.getPaymentMode() != null ? invoice.getPaymentMode() : "CASH").append("</p>")
            .append("</div>");

        html.append("<div class='section'><h3>Dispensed Medications &amp; Dosage Instructions</h3><table><thead><tr><th>Medicine Name</th><th>Batch #</th><th>Expiry</th><th>Qty</th><th>Unit Price</th><th>Dosage / Instructions</th><th style='text-align:right;'>Total (₹)</th></tr></thead><tbody>");

        if (invoice.getItems() != null && !invoice.getItems().isEmpty()) {
            for (com.clinical.pharmacy.entity.PharmacyInvoiceItemEntity item : invoice.getItems()) {
                html.append("<tr>")
                    .append("<td><b>").append(item.getMedicineName()).append("</b></td>")
                    .append("<td>").append(item.getBatchNumber()).append("</td>")
                    .append("<td>").append(item.getExpiryDate() != null ? item.getExpiryDate() : "N/A").append("</td>")
                    .append("<td>").append(item.getQuantity()).append("</td>")
                    .append("<td>₹ ").append(item.getUnitPrice()).append("</td>")
                    .append("<td>").append(item.getDosageInstruction() != null ? item.getDosageInstruction() : "As directed").append("</td>")
                    .append("<td style='text-align:right;'>₹ ").append(item.getLineTotal()).append("</td>")
                    .append("</tr>");
            }
        }

        html.append("<tr><td colspan='6' style='text-align:right;'><b>Subtotal:</b></td><td style='text-align:right;'>₹ ").append(invoice.getSubtotal()).append("</td></tr>")
            .append("<tr><td colspan='6' style='text-align:right;'>Discount Amount:</td><td style='text-align:right;'>- ₹ ").append(invoice.getDiscountAmount()).append("</td></tr>")
            .append("<tr><td colspan='6' style='text-align:right;'>GST / Tax (5%):</td><td style='text-align:right;'>+ ₹ ").append(invoice.getTaxAmount()).append("</td></tr>")
            .append("<tr style='font-weight:bold; background-color:#F1F5F9;'><td colspan='6' style='text-align:right;'>Net Payable Amount:</td><td style='text-align:right;'>₹ ").append(invoice.getTotalAmount()).append("</td></tr>")
            .append("</tbody></table></div>");

        html.append("<br/><div style='margin-top:20px;'><p style='font-size:11px; color:#64748B;'>* Medicines once sold will not be taken back or exchanged. Please consult your physician before taking medications.</p></div>");

        appendReportFooter(html);
        return html.toString();
    }

    // 3d. Official Unified OPD Master Hospital Bill & Settlement Receipt HTML
    public String generateMasterOpdInvoiceHtml(com.clinical.billing.entity.InvoiceEntity invoice, PatientEntity patient, List<LabOrderEntity> labOrders, List<com.clinical.pharmacy.entity.PharmacyInvoiceEntity> pharmacyInvoices) {
        StringBuilder html = new StringBuilder();
        appendReportHeader(html, "UNIFIED OPD MASTER HOSPITAL BILL & SETTLEMENT RECEIPT");

        String pName = patient != null ? patient.getFirstName() + " " + patient.getLastName() : (invoice.getPatientId() != null ? invoice.getPatientId() : "OPD Walk-in Patient");

        html.append("<div class='section'>")
            .append("<p><b>Master Invoice No:</b> ").append(invoice.getInvoiceId()).append(" &nbsp;&nbsp;&nbsp;&nbsp; <b>Date / Time:</b> ").append(invoice.getCreatedAt() != null ? invoice.getCreatedAt().format(DATE_FORMATTER) : LocalDateTime.now().format(DATE_FORMATTER)).append("</p>")
            .append("<p><b>Visit ID:</b> ").append(invoice.getVisitId()).append(" &nbsp;&nbsp;&nbsp;&nbsp; <b>Patient Name:</b> ").append(pName).append("</p>")
            .append("<p><b>Attending Doctor:</b> ").append(invoice.getDoctorName() != null ? invoice.getDoctorName() : "Dr. Prescriber").append(" &nbsp;&nbsp;&nbsp;&nbsp; <b>Payment Mode:</b> ").append(invoice.getPaymentMode() != null ? invoice.getPaymentMode() : "CASH").append("</p>")
            .append("<p><b>Payment Status:</b> <span class='badge ").append("PAID".equalsIgnoreCase(invoice.getPaymentStatus()) ? "paid" : "unpaid").append("'>").append(invoice.getPaymentStatus()).append("</span> &nbsp;&nbsp; <b>Remarks:</b> ").append(invoice.getRemarks() != null ? invoice.getRemarks() : "Settled at OPD Counter").append("</p>")
            .append("</div>");

        html.append("<div class='section'><h3>Itemized Departmental Billing Summary</h3><table><thead><tr><th>Department / Service Category</th><th>Description / Items Summary</th><th style='text-align:right;'>Amount (₹)</th></tr></thead><tbody>");

        // 1. Doctor Consultation Fee
        html.append("<tr>")
            .append("<td><b>Doctor OPD Consultation</b></td>")
            .append("<td>OPD Specialist Consultation Fee (").append(invoice.getDoctorName() != null ? invoice.getDoctorName() : "Doctor").append(")</td>")
            .append("<td style='text-align:right;'>₹ ").append(invoice.getConsultationFee() != null ? invoice.getConsultationFee() : "0.00").append("</td>")
            .append("</tr>");

        // 2. Laboratory Diagnostics
        StringBuilder labDesc = new StringBuilder();
        if (labOrders != null && !labOrders.isEmpty()) {
            for (LabOrderEntity lo : labOrders) {
                if (labDesc.length() > 0) labDesc.append(", ");
                labDesc.append(lo.getTestName());
            }
        } else {
            labDesc.append("No lab tests requisitions");
        }
        html.append("<tr>")
            .append("<td><b>Laboratory &amp; Diagnostics</b></td>")
            .append("<td>").append(labDesc.toString()).append("</td>")
            .append("<td style='text-align:right;'>₹ ").append(invoice.getLabFee() != null ? invoice.getLabFee() : "0.00").append("</td>")
            .append("</tr>");

        // 3. Pharmacy Dispensing
        StringBuilder pharmDesc = new StringBuilder();
        if (pharmacyInvoices != null && !pharmacyInvoices.isEmpty()) {
            for (com.clinical.pharmacy.entity.PharmacyInvoiceEntity pi : pharmacyInvoices) {
                if (pharmDesc.length() > 0) pharmDesc.append(", ");
                pharmDesc.append("Invoice ").append(pi.getInvoiceId());
            }
        } else {
            pharmDesc.append("No pharmacy medications dispensed");
        }
        html.append("<tr>")
            .append("<td><b>Pharmacy &amp; Medications</b></td>")
            .append("<td>").append(pharmDesc.toString()).append("</td>")
            .append("<td style='text-align:right;'>₹ ").append(invoice.getPharmacyFee() != null ? invoice.getPharmacyFee() : "0.00").append("</td>")
            .append("</tr>");

        // Subtotal, Discount, Tax & Net Total
        html.append("<tr><td colspan='2' style='text-align:right;'><b>Subtotal Amount:</b></td><td style='text-align:right;'>₹ ").append(invoice.getSubtotal()).append("</td></tr>")
            .append("<tr><td colspan='2' style='text-align:right;'>Discount Amount:</td><td style='text-align:right;'>- ₹ ").append(invoice.getDiscountAmount()).append("</td></tr>")
            .append("<tr><td colspan='2' style='text-align:right;'>GST / Service Tax (5%):</td><td style='text-align:right;'>+ ₹ ").append(invoice.getTaxAmount()).append("</td></tr>")
            .append("<tr style='font-weight:bold; background-color:#F1F5F9;'><td colspan='2' style='text-align:right;'>Net Settled Amount Paid:</td><td style='text-align:right; font-size:14px; color:#059669;'>₹ ").append(invoice.getTotalAmount()).append("</td></tr>")
            .append("</tbody></table></div>");

        html.append("<br/><div style='margin-top:20px;'><p style='font-size:11px; color:#64748B;'>* Computer generated OPD receipt. Valid without physical signature. Thank you for choosing our healthcare center.</p></div>");

        appendReportFooter(html);
        return html.toString();
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

    // Web Report Printing Execution
    public void printHtmlDocument(String htmlContent) {
        // Web printing handled natively via HTML5 window.print() or PDF export
        System.out.println("Printing HTML Document (Length: " + (htmlContent != null ? htmlContent.length() : 0) + " chars)");
    }

    private void appendReportHeader(StringBuilder html, String title) {
        String clinicName = clinicSettingService != null ? clinicSettingService.getSetting("CLINIC_NAME", "Apex Multispecialty Clinic") : "Apex Multispecialty Clinic";
        String regNo = clinicSettingService != null ? clinicSettingService.getSetting("REGISTRATION_NO", "REG-2026-CLINIC-88") : "REG-2026-CLINIC-88";
        String address = clinicSettingService != null ? clinicSettingService.getSetting("ADDRESS", "123 Healthcare Boulevard, Tech City") : "123 Healthcare Boulevard, Tech City";
        String phone = clinicSettingService != null ? clinicSettingService.getSetting("PHONE", "+91 9988776655") : "+91 9988776655";
        String email = clinicSettingService != null ? clinicSettingService.getSetting("EMAIL", "contact@apexclinic.com") : "contact@apexclinic.com";

        String contactInfo = address + " | Ph: " + phone + " | Email: " + email + " | Reg No: " + regNo;

        html.append("<!DOCTYPE html><html><head><style>")
            .append("body { font-family: 'Helvetica Neue', Arial, sans-serif; margin: 20px; color: #1E293B; }")
            .append(".header { text-align: center; border-bottom: 2px solid #0284C7; padding-bottom: 10px; margin-bottom: 20px; }")
            .append(".header h1 { margin: 0; color: #0284C7; font-size: 22px; }")
            .append(".header h2 { margin: 5px 0 0 0; color: #475569; font-size: 14px; text-transform: uppercase; letter-spacing: 1px; }")
            .append(".header p { margin: 4px 0 0 0; color: #64748B; font-size: 12px; }")
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
            .append("<div class='header'><h1>").append(clinicName.toUpperCase()).append("</h1><p>").append(contactInfo).append("</p><h2>").append(title).append("</h2></div>");
    }

    private void appendReportFooter(StringBuilder html) {
        html.append("<div class='footer'>")
            .append("<p>ClinicalOS Enterprise Management Platform | Confidential Medical Document | Printed: ").append(LocalDateTime.now().format(DATE_FORMATTER)).append("</p>")
            .append("</div></body></html>");
    }
}

