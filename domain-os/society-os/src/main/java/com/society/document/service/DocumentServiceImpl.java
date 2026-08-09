package com.society.document.service;

import com.society.document.entity.DocumentEntity;
import com.society.document.repository.DocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;

    public DocumentServiceImpl(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @Override
    public DocumentEntity saveDocument(DocumentEntity document) {
        return documentRepository.save(document);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DocumentEntity> getDocumentById(Integer id) {
        return documentRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentEntity> getAllDocuments() {
        return documentRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentEntity> getDocumentsByCategory(String category) {
        return documentRepository.findByCategory(category);
    }

    @Override
    public void deleteDocument(Integer id) {
        documentRepository.deleteById(id);
    }
}
