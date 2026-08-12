package com.clinical.pharmacy.service;

import com.clinical.pharmacy.entity.MedicineInventoryEntity;
import com.clinical.pharmacy.repository.MedicineInventoryRepository;
import com.clinical.prescription.entity.PrescriptionEntity;
import com.clinical.prescription.entity.PrescriptionItemEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PharmacyService {
    MedicineInventoryEntity addOrUpdateStock(MedicineInventoryEntity medicine);
    boolean dispensePrescription(PrescriptionEntity prescription);
    List<MedicineInventoryEntity> searchMedicine(String medicineName);
    List<MedicineInventoryEntity> getAllStock();
}

@Service
@Transactional
class PharmacyServiceImpl implements PharmacyService {

    private final MedicineInventoryRepository inventoryRepository;

    public PharmacyServiceImpl(MedicineInventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public MedicineInventoryEntity addOrUpdateStock(MedicineInventoryEntity medicine) {
        return inventoryRepository.save(medicine);
    }

    @Override
    public boolean dispensePrescription(PrescriptionEntity prescription) {
        for (PrescriptionItemEntity item : prescription.getItems()) {
            List<MedicineInventoryEntity> stock = inventoryRepository.findByMedicineName(item.getMedicineName());
            if (!stock.isEmpty()) {
                MedicineInventoryEntity batch = stock.get(0);
                if (batch.getQuantity() > 0) {
                    batch.setQuantity(batch.getQuantity() - 1);
                    inventoryRepository.save(batch);
                }
            }
        }
        prescription.setStatus("DISPENSED");
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicineInventoryEntity> searchMedicine(String medicineName) {
        return inventoryRepository.findByMedicineName(medicineName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicineInventoryEntity> getAllStock() {
        return inventoryRepository.findAll();
    }
}
