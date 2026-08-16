package com.clinical.ui.web;

import com.clinical.pharmacy.entity.MedicineInventoryEntity;
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

    public WebPharmacyController(PharmacyService pharmacyService) {
        this.pharmacyService = pharmacyService;
    }

    @GetMapping("/pharmacy")
    public String listPharmacy(
            @RequestParam(value = "added", required = false) Boolean added,
            Model model) {
        model.addAttribute("pageTitle", "Pharmacy Inventory & Stock");
        model.addAttribute("activeTab", "pharmacy");

        List<MedicineInventoryEntity> medicines = pharmacyService.getAllStock();
        model.addAttribute("medicines", medicines);
        model.addAttribute("totalCount", medicines.size());
        model.addAttribute("showSuccessAlert", Boolean.TRUE.equals(added));

        return "pharmacy";
    }

    @GetMapping("/pharmacy/add")
    public String addStockForm(Model model) {
        model.addAttribute("pageTitle", "Add New Medicine to Stock");
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
        return "redirect:/pharmacy?added=true";
    }
}
