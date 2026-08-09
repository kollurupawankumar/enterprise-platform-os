package com.society.governance.repository;

import com.society.governance.entity.ManagingCommitteeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ManagingCommitteeRepository extends JpaRepository<ManagingCommitteeEntity, Integer> {

    @Query("SELECT mc FROM ManagingCommitteeEntity mc LEFT JOIN FETCH mc.member")
    List<ManagingCommitteeEntity> findAllWithMembers();

    List<ManagingCommitteeEntity> findByDesignationAndStatus(String designation, String status);

    List<ManagingCommitteeEntity> findByStatus(String status);
}
