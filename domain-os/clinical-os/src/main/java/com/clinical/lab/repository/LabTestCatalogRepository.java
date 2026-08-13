package com.clinical.lab.repository;

import com.clinical.lab.entity.LabTestCatalogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LabTestCatalogRepository extends JpaRepository<LabTestCatalogEntity, Long> {
    Optional<LabTestCatalogEntity> findByTestCode(String testCode);
    List<LabTestCatalogEntity> findByCategory(String category);
}
