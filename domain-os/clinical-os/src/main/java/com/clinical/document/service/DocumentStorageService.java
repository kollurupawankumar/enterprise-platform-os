package com.clinical.document.service;

import org.springframework.stereotype.Service;

import java.io.File;

public interface DocumentStorageService {
    String getPatientDocumentPath(String patientId, String visitId, String filename);
    File resolveDocumentFile(String relativePath);
}

@Service
class DocumentStorageServiceImpl implements DocumentStorageService {

    private static final String STORAGE_BASE_DIR = "uploads/clinical-documents/";

    @Override
    public String getPatientDocumentPath(String patientId, String visitId, String filename) {
        return String.format("%spatient/%s/visits/%s/%s", STORAGE_BASE_DIR, patientId, visitId, filename);
    }

    @Override
    public File resolveDocumentFile(String relativePath) {
        File file = new File(relativePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        return file;
    }
}
