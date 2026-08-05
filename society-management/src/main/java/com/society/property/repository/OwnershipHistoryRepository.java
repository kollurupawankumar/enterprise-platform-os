package com.society.property.repository;

import com.society.property.entity.OwnershipHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OwnershipHistoryRepository extends JpaRepository<OwnershipHistoryEntity, Integer> {

    @Query("SELECT h FROM OwnershipHistoryEntity h LEFT JOIN FETCH h.member WHERE h.property.id = :propertyId ORDER BY h.id DESC")
    List<OwnershipHistoryEntity> findByPropertyId(Integer propertyId);

    List<OwnershipHistoryEntity> findByMemberId(Integer memberId);
}
