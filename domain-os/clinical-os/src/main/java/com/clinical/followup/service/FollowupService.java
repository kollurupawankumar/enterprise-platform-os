package com.clinical.followup.service;

import com.clinical.followup.entity.FollowupEntity;
import com.clinical.followup.repository.FollowupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface FollowupService {
    FollowupEntity scheduleFollowup(String visitId, String patientId, String doctorName, LocalDate followupDate, String reason);
    List<FollowupEntity> getDoctorFollowups(String doctorName, LocalDate date);
}

@Service
@Transactional
class FollowupServiceImpl implements FollowupService {

    private final FollowupRepository followupRepository;

    public FollowupServiceImpl(FollowupRepository followupRepository) {
        this.followupRepository = followupRepository;
    }

    @Override
    public FollowupEntity scheduleFollowup(String visitId, String patientId, String doctorName, LocalDate followupDate, String reason) {
        FollowupEntity followup = new FollowupEntity();
        followup.setVisitId(visitId);
        followup.setPatientId(patientId);
        followup.setDoctorName(doctorName);
        followup.setFollowupDate(followupDate);
        followup.setReason(reason);
        followup.setStatus("SCHEDULED");
        return followupRepository.save(followup);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FollowupEntity> getDoctorFollowups(String doctorName, LocalDate date) {
        return followupRepository.findByDoctorNameAndFollowupDate(doctorName, date != null ? date : LocalDate.now());
    }
}
