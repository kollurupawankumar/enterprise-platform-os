package com.society.share.repository;

import com.society.share.entity.ShareTransferHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShareTransferHistoryRepository extends JpaRepository<ShareTransferHistoryEntity, Integer> {

    List<ShareTransferHistoryEntity> findByCertificateIdOrderByIdDesc(Integer certificateId);
}
