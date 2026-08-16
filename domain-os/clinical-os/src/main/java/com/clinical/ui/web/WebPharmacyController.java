package com.clinical.ui.web;

import com.clinical.patient.entity.PatientEntity;
import com.clinical.patient.service.PatientService;
import com.clinical.pharmacy.entity.MedicineInventoryEntity;
import com.clinical.pharmacy.repository.MedicineInventoryRepository;
import com.clinical.pharmacy.service.PharmacyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
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
        if (med != null && med.getQuantity() != null) {
            int updated = Math.max(0, med.getQuantity() - quantity);
            med.setQuantity(updated);
            pharmacyService.addOrUpdateStock(med);
        }

        return "redirect:/pharmacy/dispense?dispensed=true";
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
