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
    private final com.clinical.admin.service.IdPatternGeneratorService idGenerator;

    public ReferralService(ReferralRepository referralRepository, com.clinical.admin.service.IdPatternGeneratorService idGenerator) {
        this.referralRepository = referralRepository;
        this.idGenerator = idGenerator;
    }

    public ReferralEntity createReferral(ReferralEntity referral) {
        if (referral.getReferralId() == null || referral.getReferralId().isEmpty()) {
            long count = referralRepository.count() + 1;
            referral.setReferralId(idGenerator.generateId("REFERRAL_ID_FORMAT", "REF-{YYYY}-{SEQ6}", count));
        }
        return referralRepository.save(referral);
    }

    @Transactional(readOnly = true)
    public List<ReferralEntity> getPatientReferrals(String patientId) {
        return referralRepository.findByPatientId(patientId);
    }
}
