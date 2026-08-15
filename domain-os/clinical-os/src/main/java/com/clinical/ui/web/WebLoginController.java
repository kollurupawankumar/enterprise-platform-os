package com.clinical.ui.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class WebLoginController {

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String processLogin() {
        return "redirect:/dashboard";
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/login";
    }
}
