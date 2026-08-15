package com.society.web;

import com.society.member.dto.MemberDto;
import com.society.member.service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class WebMemberController {

    private final MemberService memberService;

    public WebMemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/members")
    public String listMembers(@RequestParam(value = "query", required = false) String query, Model model) {
        model.addAttribute("pageTitle", "Member Directory");
        model.addAttribute("activeTab", "members");

        List<MemberDto> allMembers = memberService.findAll();
        List<MemberDto> members;

        if (query != null && !query.isBlank()) {
            String q = query.trim().toLowerCase();
            members = allMembers.stream()
                    .filter(m -> (m.memberNumber() != null && m.memberNumber().toLowerCase().contains(q)) ||
                                 (m.firstName() != null && m.firstName().toLowerCase().contains(q)) ||
                                 (m.lastName() != null && m.lastName().toLowerCase().contains(q)) ||
                                 (m.mobileNumber() != null && m.mobileNumber().contains(q)))
                    .collect(Collectors.toList());
        } else {
            members = allMembers;
        }

        model.addAttribute("members", members);
        model.addAttribute("query", query != null ? query : "");

        return "members";
    }
}
