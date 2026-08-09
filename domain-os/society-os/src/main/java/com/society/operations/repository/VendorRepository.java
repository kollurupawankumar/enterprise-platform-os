package com.society.operations.repository;

import com.society.operations.entity.VendorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendorRepository extends JpaRepository<VendorEntity, Integer> {
    List<VendorEntity> findByCategory(String category);
}
