package com.society.operations.repository;

import com.society.operations.entity.AssetServiceLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetServiceLogRepository extends JpaRepository<AssetServiceLogEntity, Integer> {

    @Query("SELECT s FROM AssetServiceLogEntity s WHERE s.asset.id = :assetId ORDER BY s.id DESC")
    List<AssetServiceLogEntity> findByAssetId(@Param("assetId") Integer assetId);

    @Query("SELECT s FROM AssetServiceLogEntity s LEFT JOIN FETCH s.asset ORDER BY s.id DESC")
    List<AssetServiceLogEntity> findAllWithAsset();
}
