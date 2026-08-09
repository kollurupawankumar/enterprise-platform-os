package com.society.member.repository;

import com.society.member.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Integer> {

    Optional<MemberEntity> findFirstByOrderByIdDesc();

    boolean existsByMemberNumber(String memberNumber);

    boolean existsByMembershipNumber(String membershipNumber);

    boolean existsByMembershipNumberAndIdNot(String membershipNumber, Integer id);

    Optional<MemberEntity> findByMemberNumber(String memberNumber);

    Optional<MemberEntity> findByMembershipNumber(String membershipNumber);

    List<MemberEntity> findByActiveTrue();

    @Query("""
            SELECT m
            FROM MemberEntity m
            WHERE lower(m.memberNumber) LIKE lower(concat('%', :keyword, '%'))
               OR lower(m.membershipNumber) LIKE lower(concat('%', :keyword, '%'))
               OR lower(m.firstName) LIKE lower(concat('%', :keyword, '%'))
               OR lower(m.lastName) LIKE lower(concat('%', :keyword, '%'))
               OR lower(m.mobileNumber) LIKE lower(concat('%', :keyword, '%'))
            ORDER BY m.memberNumber
            """)
    List<MemberEntity> search(@Param("keyword") String keyword);
}