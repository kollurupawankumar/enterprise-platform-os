package com.society.member.repository;

import com.society.member.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository
        extends JpaRepository<MemberEntity, Integer> {

    boolean existsByMemberNumber(String memberNumber);

    boolean existsByMembershipNumber(String membershipNumber);

    Optional<MemberEntity> findFirstByOrderByIdDesc();

}