package com.society.member.repository;

import com.society.member.entity.JointOwnerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JointOwnerRepository extends JpaRepository<JointOwnerEntity, Integer> {
    List<JointOwnerEntity> findByMemberId(Integer memberId);
}
