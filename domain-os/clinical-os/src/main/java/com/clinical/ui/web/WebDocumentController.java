package com.clinical.ui.web;

import com.clinical.document.entity.PatientDocumentEntity;
import com.clinical.document.repository.PatientDocumentRepository;
import com.clinical.document.service.PatientDocumentService;
import com.clinical.patient.entity.PatientEntity;
import com.clinical.patient.service.PatientService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class WebDocumentController {

    private final PatientDocumentService documentService;
    private final PatientDocumentRepository documentRepository;
    private final PatientService patientService;

    public WebDocumentController(
            PatientDocumentService documentService,
            PatientDocumentRepository documentRepository,
            PatientService patientService) {
        this.documentService = documentService;
        this.documentRepository = documentRepository;
        this.patientService = patientService;
    }

    @GetMapping("/documents")
    public String listDocuments(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "uploaded", required = false) Boolean uploaded,
            Model model) {
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
        model.addAttribute("totalCount", docs.size());
        model.addAttribute("query", query != null ? query : "");
        model.addAttribute("showSuccessAlert", Boolean.TRUE.equals(uploaded));

        return "documents";
    }

    @GetMapping("/documents/upload")
    public String uploadDocumentForm(Model model) {
        model.addAttribute("pageTitle", "Upload EMR Medical Document");
        model.addAttribute("activeTab", "document-upload");

        List<PatientEntity> patients = patientService.getAllPatients();
        model.addAttribute("patients", patients);

        return "document_upload";
    }

    @PostMapping("/documents/upload")
    public String uploadDocument(
            @RequestParam("patientId") String patientId,
            @RequestParam("documentName") String documentName,
            @RequestParam(value = "category", defaultValue = "GENERAL") String category) {

        PatientDocumentEntity doc = new PatientDocumentEntity();
        doc.setDocumentId("DOC-" + System.currentTimeMillis() % 10000);
        doc.setPatientId(patientId);
        doc.setVisitId("VISIT-" + System.currentTimeMillis() % 10000);
        doc.setDocumentName(documentName.trim());
        doc.setDocumentType(category.trim());
        doc.setFilePath("/storage/vault/" + System.currentTimeMillis() + "_" + documentName.replaceAll(" ", "_"));
        doc.setFileSize("2.0 MB");
        doc.setUploadedBy("Dr. Pawan Kumar");

        documentRepository.save(doc);
        return "redirect:/documents?uploaded=true";
    }
}
