package com.society.dashboard.service;

import com.society.dashboard.dto.DashboardSummaryDto;
import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Override
    public DashboardSummaryDto getSummary() {

        return new DashboardSummaryDto(
                0,
                0,
                0,
                true
        );

    }

}