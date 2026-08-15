package com.clinical.ui.web;

import com.clinical.document.entity.PatientDocumentEntity;
import com.clinical.document.service.PatientDocumentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class WebDocumentController {

    private final PatientDocumentService documentService;

    public WebDocumentController(PatientDocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/documents")
    public String listDocuments(@RequestParam(value = "query", required = false) String query, Model model) {
        model.addAttribute("pageTitle", "Digital EMR Document Vault");
        model.addAttribute("activeTab", "documents");

        List<PatientDocumentEntity> allDocs = documentService.getAllDocuments();
        List<PatientDocumentEntity> docs;

        if (query != null && !query.isBlank()) {
            String q = query.trim().toLowerCase();
            docs = allDocs.stream()
                    .filter(d -> (d.getDocumentId() != null && d.getDocumentId().toLowerCase().contains(q)) ||
                                 (d.getPatientId() != null && d.getPatientId().toLowerCase().contains(q)) ||
                                 (d.getDocumentName() != null && d.getDocumentName().toLowerCase().contains(q)))
                    .collect(Collectors.toList());
        } else {
            docs = allDocs;
        }

        model.addAttribute("documents", docs);
        model.addAttribute("query", query != null ? query : "");

        return "documents";
    }
}
