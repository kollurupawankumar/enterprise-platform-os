package com.society.governance.repository;

import com.society.governance.entity.ManagingCommitteeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ManagingCommitteeRepository extends JpaRepository<ManagingCommitteeEntity, Integer> {
    List<ManagingCommitteeEntity> findByStatus(String status);
}
