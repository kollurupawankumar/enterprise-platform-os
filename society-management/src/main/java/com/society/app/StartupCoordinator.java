package com.society.app;

import com.society.common.navigation.NavigationManager;
import com.society.common.navigation.View;
import com.society.society.service.SocietyService;
import com.society.user.service.UserService;
import org.springframework.stereotype.Component;

@Component
public class StartupCoordinator {

    private final NavigationManager navigationManager;
    private final SocietyService societyService;
    private final UserService userService;

    public StartupCoordinator(
            NavigationManager navigationManager,
            SocietyService societyService,
            UserService userService) {

        this.navigationManager = navigationManager;
        this.societyService = societyService;
        this.userService = userService;
    }

    public void start() {

        if (societyService.exists() && userService.hasAdminUser()) {
            navigationManager.navigate(View.LOGIN);
        } else {
            navigationManager.navigate(View.SOCIETY_SETUP);
        }
    }
}