package com.society.web;

import com.society.finance.repository.BankAccountRepository;
import com.society.finance.repository.InvestmentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebFinanceController {

    private final BankAccountRepository bankAccountRepository;
    private final InvestmentRepository investmentRepository;

    public WebFinanceController(BankAccountRepository bankAccountRepository, InvestmentRepository investmentRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.investmentRepository = investmentRepository;
    }

    @GetMapping("/finance")
    public String listFinance(Model model) {
        model.addAttribute("pageTitle", "Bank Accounts & Fixed Deposit Investments");
        model.addAttribute("activeTab", "finance");
        model.addAttribute("bankAccounts", bankAccountRepository.findAll());
        model.addAttribute("investments", investmentRepository.findAll());
        return "finance";
    }
}
