package com.society.header.controller;

import com.society.common.navigation.NavigationManager;
import com.society.common.navigation.View;
import com.society.society.dto.SocietyDto;
import com.society.society.runtime.SocietyProvider;
import com.society.user.context.UserContext;
import com.society.user.entity.UserEntity;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.springframework.stereotype.Component;

@Component
public class HeaderController {

    private final SocietyProvider societyProvider;
    private final UserContext userContext;
    private final NavigationManager navigationManager;

    @FXML private Label societyNameLabel;
    @FXML private Label registrationNumberLabel;
    @FXML private Label financialYearLabel;

    @FXML private Label userBadgeLabel;
    @FXML private Label userRoleBadgeLabel;

    public HeaderController(
            SocietyProvider societyProvider,
            UserContext userContext,
            NavigationManager navigationManager) {
        this.societyProvider = societyProvider;
        this.userContext = userContext;
        this.navigationManager = navigationManager;
    }

    @FXML
    public void initialize() {
        loadSociety();
        loadUserProfile();
    }

    public void refresh() {
        loadSociety();
        loadUserProfile();
    }

    private void loadUserProfile() {
        if (userContext.isLoggedIn()) {
            UserEntity u = userContext.getCurrentUser();
            userBadgeLabel.setText(u.getFirstName() + " " + (u.getLastName() != null ? u.getLastName() : ""));
            userRoleBadgeLabel.setText("[" + u.getRole() + "]");
        } else {
            userBadgeLabel.setText("Guest User");
            userRoleBadgeLabel.setText("");
        }
    }

    @FXML
    private void handleLogout() {
        userContext.clear();
        navigationManager.navigate(View.LOGIN);
    }

    private void loadSociety() {

        if (!societyProvider.hasSociety()) {

            societyNameLabel.setText("Society Office OS");
            registrationNumberLabel.setText("");
            financialYearLabel.setText("");

            return;
        }

        SocietyDto society = societyProvider.getCurrentSociety();

        societyNameLabel.setText(society.name());

        registrationNumberLabel.setText(
                "Registration No : "
                        + society.registrationNumber());

        financialYearLabel.setText(
                "Financial Year Starts : "
                        + society.financialYearStartMonth());

    }

}