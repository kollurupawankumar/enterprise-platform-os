package com.society.web;

import com.society.finance.repository.BankAccountRepository;
import com.society.finance.repository.InvestmentRepository;
import com.society.member.dto.MemberDto;
import com.society.member.service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class WebDashboardController {

    private final MemberService memberService;
    private final BankAccountRepository bankAccountRepository;
    private final InvestmentRepository investmentRepository;

    public WebDashboardController(
            MemberService memberService,
            BankAccountRepository bankAccountRepository,
            InvestmentRepository investmentRepository) {
        this.memberService = memberService;
        this.bankAccountRepository = bankAccountRepository;
        this.investmentRepository = investmentRepository;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "Society OS Dashboard");
        model.addAttribute("activeTab", "dashboard");

        List<MemberDto> members = memberService.findAll();
        model.addAttribute("totalMembers", members.size());
        model.addAttribute("totalProperties", 160);
        model.addAttribute("members", members);

        double totalBank = bankAccountRepository.findAll().stream()
                .mapToDouble(b -> b.getBalance() != null ? b.getBalance() : 0.0)
                .sum();

        double totalFd = investmentRepository.findAll().stream()
                .mapToDouble(i -> i.getPrincipalAmount() != null ? i.getPrincipalAmount() : 0.0)
                .sum();

        model.addAttribute("formattedBankBalance", String.format("₹%,.2f", totalBank));
        model.addAttribute("formattedFdReserve", String.format("₹%,.2f", totalFd));

        return "dashboard";
    }
}
