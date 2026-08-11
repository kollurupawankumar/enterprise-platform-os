package com.clinical.followup.repository;

import com.clinical.followup.entity.FollowupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FollowupRepository extends JpaRepository<FollowupEntity, Long> {
    List<FollowupEntity> findByDoctorNameAndFollowupDate(String doctorName, LocalDate followupDate);
    List<FollowupEntity> findByPatientId(String patientId);
}
