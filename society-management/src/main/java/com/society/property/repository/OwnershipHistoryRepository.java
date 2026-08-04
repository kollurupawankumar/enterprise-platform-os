package com.society.property.repository;

import com.society.property.entity.OwnershipHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OwnershipHistoryRepository extends JpaRepository<OwnershipHistoryEntity, Integer> {
    List<OwnershipHistoryEntity> findByPropertyId(Integer propertyId);
    List<OwnershipHistoryEntity> findByMemberId(Integer memberId);
}
