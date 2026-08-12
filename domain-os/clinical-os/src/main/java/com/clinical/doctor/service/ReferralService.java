package com.clinical.doctor.service;

import com.clinical.doctor.entity.ReferralEntity;
import com.clinical.doctor.repository.ReferralRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ReferralService {

    private final ReferralRepository referralRepository;

    public ReferralService(ReferralRepository referralRepository) {
        this.referralRepository = referralRepository;
    }

    public ReferralEntity createReferral(ReferralEntity referral) {
        if (referral.getReferralId() == null || referral.getReferralId().isEmpty()) {
            long count = referralRepository.count() + 1;
            referral.setReferralId(String.format("REF-%06d", count));
        }
        return referralRepository.save(referral);
    }

    @Transactional(readOnly = true)
    public List<ReferralEntity> getPatientReferrals(String patientId) {
        return referralRepository.findByPatientId(patientId);
    }
}
