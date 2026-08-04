package com.society.share.repository;

import com.society.share.entity.ShareCertificateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ShareCertificateRepository extends JpaRepository<ShareCertificateEntity, Integer> {

    Optional<ShareCertificateEntity> findFirstByOrderByIdDesc();

    boolean existsByCertificateNumber(String certificateNumber);

    List<ShareCertificateEntity> findByMemberId(Integer memberId);

    @Query("""
            SELECT sc
            FROM ShareCertificateEntity sc
            JOIN sc.member m
            WHERE lower(sc.certificateNumber) LIKE lower(concat('%', :keyword, '%'))
               OR lower(m.firstName) LIKE lower(concat('%', :keyword, '%'))
               OR lower(m.lastName) LIKE lower(concat('%', :keyword, '%'))
               OR lower(m.memberNumber) LIKE lower(concat('%', :keyword, '%'))
               OR lower(m.membershipNumber) LIKE lower(concat('%', :keyword, '%'))
            ORDER BY sc.id DESC
            """)
    List<ShareCertificateEntity> search(@Param("keyword") String keyword);

    @Query("""
            SELECT COUNT(sc) > 0
            FROM ShareCertificateEntity sc
            WHERE sc.status = 'ACTIVE'
              AND NOT (sc.toShareNumber < :fromShare OR sc.fromShareNumber > :toShare)
            """)
    boolean isShareRangeOverlapping(@Param("fromShare") Integer fromShare, @Param("toShare") Integer toShare);

    @Query("SELECT COALESCE(SUM(sc.totalShares), 0) FROM ShareCertificateEntity sc WHERE sc.status = 'ACTIVE'")
    long sumTotalActiveShares();

    @Query("SELECT COUNT(sc) FROM ShareCertificateEntity sc WHERE sc.status = 'ACTIVE'")
    long countActiveCertificates();
}
