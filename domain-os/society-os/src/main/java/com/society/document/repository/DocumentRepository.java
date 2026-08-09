package com.society.document.repository;

import com.society.document.entity.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<DocumentEntity, Integer> {
    List<DocumentEntity> findByCategory(String category);
}
