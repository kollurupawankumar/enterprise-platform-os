package com.society.member.repository;

import com.society.member.entity.NomineeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NomineeRepository extends JpaRepository<NomineeEntity, Integer> {
    List<NomineeEntity> findByMemberId(Integer memberId);
}
