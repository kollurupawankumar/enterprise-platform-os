package com.society.operations.repository;

import com.society.operations.entity.AssetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetRepository extends JpaRepository<AssetEntity, Integer> {

    @Query("SELECT a FROM AssetEntity a LEFT JOIN FETCH a.amcVendor")
    List<AssetEntity> findAllWithAmcVendor();

    List<AssetEntity> findByCategory(String category);
}
