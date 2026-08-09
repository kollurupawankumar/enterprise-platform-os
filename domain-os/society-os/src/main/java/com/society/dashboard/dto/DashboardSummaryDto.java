package com.society.dashboard.dto;

public record DashboardSummaryDto(
        long totalMembers,
        long totalShares,
        long totalCertificates,
        boolean databaseConnected
) {
}