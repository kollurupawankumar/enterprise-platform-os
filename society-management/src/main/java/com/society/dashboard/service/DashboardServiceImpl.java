package com.society.dashboard.service;

import com.society.dashboard.dto.DashboardSummaryDto;
import com.society.member.repository.MemberRepository;
import com.society.share.repository.ShareCertificateRepository;
import com.society.infrastructure.health.DatabaseHealthService;
import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final MemberRepository memberRepository;
    private final ShareCertificateRepository shareCertificateRepository;
    private final DatabaseHealthService databaseHealthService;

    public DashboardServiceImpl(
            MemberRepository memberRepository,
            ShareCertificateRepository shareCertificateRepository,
            DatabaseHealthService databaseHealthService) {

        this.memberRepository = memberRepository;
        this.shareCertificateRepository = shareCertificateRepository;
        this.databaseHealthService = databaseHealthService;
    }

    @Override
    public DashboardSummaryDto getSummary() {

        long totalMembers = memberRepository.count();
        long totalShares = shareCertificateRepository.sumTotalActiveShares();
        long totalCertificates = shareCertificateRepository.countActiveCertificates();
        boolean connected = databaseHealthService.isDatabaseAvailable();

        return new DashboardSummaryDto(
                totalMembers,
                totalShares,
                totalCertificates,
                connected
        );

    }

}