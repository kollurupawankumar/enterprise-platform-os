package com.clinical.pharmacy.repository;

import com.clinical.pharmacy.entity.MedicineInventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicineInventoryRepository extends JpaRepository<MedicineInventoryEntity, Long> {
    Optional<MedicineInventoryEntity> findByMedicineNameAndBatchNumber(String medicineName, String batchNumber);
    List<MedicineInventoryEntity> findByMedicineName(String medicineName);
}
