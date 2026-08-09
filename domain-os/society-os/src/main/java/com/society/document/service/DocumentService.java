package com.society.document.service;

import com.society.document.entity.DocumentEntity;
import java.util.List;
import java.util.Optional;

public interface DocumentService {
    DocumentEntity saveDocument(DocumentEntity document);
    Optional<DocumentEntity> getDocumentById(Integer id);
    List<DocumentEntity> getAllDocuments();
    List<DocumentEntity> getDocumentsByCategory(String category);
    void deleteDocument(Integer id);
}
