package com.clinical.ui.web;

import com.clinical.patient.entity.PatientEntity;
import com.clinical.patient.service.PatientService;
import com.clinical.pharmacy.entity.MedicineInventoryEntity;
import com.clinical.pharmacy.entity.PharmacyInvoiceEntity;
import com.clinical.pharmacy.entity.PharmacyInvoiceItemEntity;
import com.clinical.pharmacy.repository.MedicineInventoryRepository;
import com.clinical.pharmacy.service.PharmacyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class WebPharmacyController {

    private final PharmacyService pharmacyService;
    private final MedicineInventoryRepository inventoryRepository;
    private final PatientService patientService;

    public WebPharmacyController(
            PharmacyService pharmacyService,
            MedicineInventoryRepository inventoryRepository,
            PatientService patientService) {
        this.pharmacyService = pharmacyService;
        this.inventoryRepository = inventoryRepository;
        this.patientService = patientService;
    }

    @GetMapping({"/pharmacy", "/pharmacy/stock"})
    public String listPharmacyStock(
            @RequestParam(value = "added", required = false) Boolean added,
            Model model) {
        model.addAttribute("pageTitle", "Pharmacy Inventory & Expiry Tracking");
        model.addAttribute("activeTab", "pharmacy-stock");

        List<MedicineInventoryEntity> medicines = pharmacyService.getAllStock();
        model.addAttribute("medicines", medicines);
        model.addAttribute("totalCount", medicines.size());
        model.addAttribute("showSuccessAlert", Boolean.TRUE.equals(added));

        return "pharmacy";
    }

    @GetMapping("/pharmacy/dispense")
    public String dispenseForm(
            @RequestParam(value = "dispensed", required = false) Boolean dispensed,
            Model model) {
        model.addAttribute("pageTitle", "OPD Pharmacy Dispensing & POS Counter");
        model.addAttribute("activeTab", "pharmacy-dispense");

        List<PatientEntity> patients = patientService.getAllPatients();
        model.addAttribute("patients", patients);

        List<MedicineInventoryEntity> medicines = pharmacyService.getAllStock();
        model.addAttribute("medicines", medicines);
        model.addAttribute("showSuccessAlert", Boolean.TRUE.equals(dispensed));

        return "pharmacy_dispense";
    }

    @PostMapping("/pharmacy/dispense")
    public String processDispensing(
            @RequestParam(value = "patientType", defaultValue = "REGISTERED") String patientType,
            @RequestParam(value = "patientId", defaultValue = "PAT-WALKIN") String patientId,
            @RequestParam(value = "walkinName", required = false) String walkinName,
            @RequestParam("medicineId") Long medicineId,
            @RequestParam(value = "quantity", defaultValue = "1") Integer quantity,
            @RequestParam(value = "paymentMode", defaultValue = "CASH") String paymentMode) {

        String targetPatient = "WALKIN".equalsIgnoreCase(patientType) && walkinName != null && !walkinName.isBlank()
                ? "WALKIN: " + walkinName.trim()
                : patientId;

        MedicineInventoryEntity med = inventoryRepository.findById(medicineId).orElse(null);

        List<PharmacyInvoiceItemEntity> items = new ArrayList<>();
        if (med != null) {
            PharmacyInvoiceItemEntity item = new PharmacyInvoiceItemEntity();
            item.setMedicineName(med.getMedicineName());
            item.setBatchNumber(med.getBatchNumber());
            item.setQuantity(quantity);
            item.setUnitPrice(med.getSellingPrice() != null ? med.getSellingPrice() : BigDecimal.valueOf(2.50));
            items.add(item);
        }

        String visitId = "VISIT-" + System.currentTimeMillis() % 10000;
        PharmacyInvoiceEntity invoice = pharmacyService.createPharmacyInvoice(
                targetPatient,
                visitId,
                "Dr. Suresh Kumar",
                items,
                BigDecimal.ZERO,
                "PAID",
                paymentMode
        );

        return "redirect:/pharmacy/receipt/" + invoice.getInvoiceId();
    }

    @GetMapping("/pharmacy/bills")
    public String listDispensedBills(
            @RequestParam(value = "dispensed", required = false) Boolean dispensed,
            Model model) {
        model.addAttribute("pageTitle", "Dispensed Pharmacy Bills & Invoices Directory");
        model.addAttribute("activeTab", "pharmacy-bills");

        List<PharmacyInvoiceEntity> invoices = pharmacyService.getAllInvoices();
        model.addAttribute("invoices", invoices);
        model.addAttribute("totalCount", invoices.size());
        model.addAttribute("showSuccessAlert", Boolean.TRUE.equals(dispensed));

        return "pharmacy_bills";
    }

    @GetMapping("/pharmacy/receipt/{invoiceId}")
    public String viewPharmacyReceipt(@PathVariable("invoiceId") String invoiceId, Model model) {
        model.addAttribute("pageTitle", "Printable Pharmacy POS Bill & Tax Invoice");

        PharmacyInvoiceEntity invoice = pharmacyService.getAllInvoices().stream()
                .filter(i -> invoiceId.equals(i.getInvoiceId()))
                .findFirst().orElse(null);

        List<PharmacyInvoiceItemEntity> items = pharmacyService.getInvoiceItems(invoiceId);

        model.addAttribute("invoice", invoice);
        model.addAttribute("items", items);

        return "pharmacy_receipt";
    }

    @GetMapping("/pharmacy/add")
    public String addStockForm(Model model) {
        model.addAttribute("pageTitle", "Add Inward Medicine Batch Stock");
        model.addAttribute("activeTab", "pharmacy-add");
        return "pharmacy_add";
    }

    @PostMapping("/pharmacy/add")
    public String addStock(
            @RequestParam("medicineName") String medicineName,
            @RequestParam("category") String category,
            @RequestParam("unitPrice") Double unitPrice,
            @RequestParam("stockQuantity") Integer stockQuantity) {

        MedicineInventoryEntity med = new MedicineInventoryEntity();
        med.setMedicineName(medicineName.trim());
        med.setCategory(category.trim());
        med.setSellingPrice(BigDecimal.valueOf(unitPrice));
        med.setQuantity(stockQuantity);
        med.setBatchNumber("BAT-" + System.currentTimeMillis() % 10000);
        med.setExpiryDate(LocalDate.now().plusYears(2));

        pharmacyService.addOrUpdateStock(med);
        return "redirect:/pharmacy/stock?added=true";
    }

    @GetMapping("/pharmacy/schedule-h1")
    public String scheduleH1Log(Model model) {
        model.addAttribute("pageTitle", "Schedule H1 & Restricted Narcotics Register");
        model.addAttribute("activeTab", "pharmacy-schedule-h1");
        return "pharmacy_schedule_h1";
    }

    @GetMapping("/pharmacy/reports")
    public String pharmacyReports(Model model) {
        model.addAttribute("pageTitle", "Pharmacy Sales & Tax Audit Analytics");
        model.addAttribute("activeTab", "pharmacy-reports");
        return "pharmacy_reports";
    }
}
