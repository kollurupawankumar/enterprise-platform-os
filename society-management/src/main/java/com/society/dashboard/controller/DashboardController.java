package com.society.dashboard.controller;

import com.society.config.SocietyConfig;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label membersLabel;

    @FXML
    private Label sharesLabel;

    @FXML
    private Label certificatesLabel;

    @FXML
    private Label databaseLabel;

    @FXML
    private Label backupLabel;

    @FXML
    private Label versionLabel;

    @FXML
    public void initialize() {

        welcomeLabel.setText(
                "Welcome to\n" +
                        SocietyConfig.societyName());

        membersLabel.setText("0");
        sharesLabel.setText("0");
        certificatesLabel.setText("0");

        databaseLabel.setText("Not Connected");
        backupLabel.setText("Never");

        versionLabel.setText(
                SocietyConfig.appVersion());

    }

}