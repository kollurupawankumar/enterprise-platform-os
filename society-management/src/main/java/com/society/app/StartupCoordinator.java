package com.society.app;

import com.society.common.navigation.NavigationManager;
import com.society.common.navigation.View;
import com.society.society.service.SocietyService;
import org.springframework.stereotype.Component;

@Component
public class StartupCoordinator {

    private final NavigationManager navigationManager;
    private final SocietyService societyService;

    public StartupCoordinator(
            NavigationManager navigationManager,
            SocietyService societyService) {

        this.navigationManager = navigationManager;
        this.societyService = societyService;
    }

    public void start() {

        if (societyService.exists()) {
            navigationManager.navigate(View.DASHBOARD);
        } else {
            navigationManager.navigate(View.SOCIETY_SETUP);
        }
    }
}