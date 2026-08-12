package com.clinical.doctor.repository;

import com.clinical.doctor.entity.ReferralEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReferralRepository extends JpaRepository<ReferralEntity, Long> {
    Optional<ReferralEntity> findByReferralId(String referralId);
    List<ReferralEntity> findByPatientId(String patientId);
}
