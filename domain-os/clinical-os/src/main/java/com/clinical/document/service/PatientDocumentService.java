package com.clinical.document.service;

import com.clinical.document.entity.PatientDocumentEntity;
import com.clinical.document.repository.PatientDocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

public interface PatientDocumentService {
    PatientDocumentEntity uploadDocument(String patientId, String visitId, String documentName, String documentType, File sourceFile, String remarks, String uploadedBy) throws IOException;
    Optional<PatientDocumentEntity> getDocumentById(String documentId);
    List<PatientDocumentEntity> getDocumentsByPatient(String patientId);
    List<PatientDocumentEntity> getDocumentsByVisit(String visitId);
    List<PatientDocumentEntity> getAllDocuments();
    void deleteDocument(String documentId);
}

@Service
@Transactional
class PatientDocumentServiceImpl implements PatientDocumentService {

    private final PatientDocumentRepository documentRepository;
    private final com.clinical.admin.service.IdPatternGeneratorService idGenerator;
    private static final String STORAGE_DIR = "data/documents/";

    public PatientDocumentServiceImpl(PatientDocumentRepository documentRepository,
                                       com.clinical.admin.service.IdPatternGeneratorService idGenerator) {
        this.documentRepository = documentRepository;
        this.idGenerator = idGenerator;

        // Ensure storage directory exists
        try {
            Files.createDirectories(Paths.get(STORAGE_DIR));
        } catch (Exception ignored) {}
    }

    @Override
    public PatientDocumentEntity uploadDocument(String patientId, String visitId, String documentName, String documentType, File sourceFile, String remarks, String uploadedBy) throws IOException {
        long count = documentRepository.count() + 1;
        String docId = idGenerator.generateId("DOCUMENT_ID_FORMAT", "DOC-{YYYY}-{SEQ6}", count);

        String fileExt = "";
        if (sourceFile.getName().contains(".")) {
            fileExt = sourceFile.getName().substring(sourceFile.getName().lastIndexOf("."));
        }

        String storedFileName = docId + "_" + System.currentTimeMillis() + fileExt;
        Path targetPath = Paths.get(STORAGE_DIR, storedFileName);
        Files.copy(sourceFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        PatientDocumentEntity doc = new PatientDocumentEntity();
        doc.setDocumentId(docId);
        doc.setPatientId(patientId);
        doc.setVisitId(visitId != null && !visitId.isBlank() ? visitId : "GENERAL-CHARTS");
        doc.setDocumentName(documentName != null && !documentName.isBlank() ? documentName : sourceFile.getName());
        doc.setDocumentType(documentType != null ? documentType : "OTHER");
        doc.setFilePath(targetPath.toAbsolutePath().toString());
        doc.setFileSize(formatFileSize(sourceFile.length()));
        doc.setUploadedBy(uploadedBy != null ? uploadedBy : "Clinical Staff");
        doc.setRemarks(remarks);

        return documentRepository.save(doc);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PatientDocumentEntity> getDocumentById(String documentId) {
        return documentRepository.findByDocumentId(documentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientDocumentEntity> getDocumentsByPatient(String patientId) {
        if (patientId == null || patientId.isBlank() || "ALL".equalsIgnoreCase(patientId)) {
            return documentRepository.findAll();
        }
        return documentRepository.findByPatientId(patientId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientDocumentEntity> getDocumentsByVisit(String visitId) {
        return documentRepository.findByVisitId(visitId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientDocumentEntity> getAllDocuments() {
        return documentRepository.findAll();
    }

    @Override
    public void deleteDocument(String documentId) {
        documentRepository.findByDocumentId(documentId).ifPresent(doc -> {
            try {
                Files.deleteIfExists(Paths.get(doc.getFilePath()));
            } catch (Exception ignored) {}
            documentRepository.delete(doc);
        });
    }

    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        char pre = "KMGTPE".charAt(exp - 1);
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
}
