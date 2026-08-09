package com.society.knowledge.repository;

import com.society.knowledge.entity.KnowledgeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KnowledgeRepository extends JpaRepository<KnowledgeEntity, Integer> {
    List<KnowledgeEntity> findByCategory(String category);
}
