package com.clinical.ui.web;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebLoginController {

    @GetMapping("/login")
    public String loginPage(
            @RequestParam(value = "logout", required = false) Boolean logout,
            @RequestParam(value = "expired", required = false) Boolean expired,
            Model model) {
        model.addAttribute("showLogoutAlert", Boolean.TRUE.equals(logout));
        model.addAttribute("showExpiredAlert", Boolean.TRUE.equals(expired));
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(
            @RequestParam(value = "username", required = false, defaultValue = "dr.pawan@clinical.os") String username,
            @RequestParam(value = "password", required = false) String password,
            HttpSession session) {

        String role = "DOCTOR";
        String displayName = "Dr. Pawan Kumar";

        if (username.contains("admin")) {
            role = "SUPER_ADMIN";
            displayName = "Super Administrator";
        } else if (username.contains("nurse")) {
            role = "NURSE_RECEPTIONIST";
            displayName = "Nurse Anjali Verma";
        } else if (username.contains("pharma")) {
            role = "PHARMACIST";
            displayName = "Pharmacist Rajesh Rao";
        } else if (username.contains("lab")) {
            role = "LAB_TECHNICIAN";
            displayName = "Technician Vikas Reddy";
        } else if (username.contains("billing")) {
            role = "BILLING_CASHIER";
            displayName = "Billing Cashier Kavita";
        } else if (username.contains("patient")) {
            role = "PATIENT";
            displayName = "Patient User";
        }

        session.setAttribute("loggedInUser", username);
        session.setAttribute("userName", displayName);
        session.setAttribute("userRole", role);

        return "redirect:/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout=true";
    }
}
