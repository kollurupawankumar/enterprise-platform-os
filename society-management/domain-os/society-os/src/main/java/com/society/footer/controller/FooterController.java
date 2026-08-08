package com.society.footer.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FooterController {

    @Value("${app.version:1.0.0}")
    private String applicationVersion;

    @FXML
    private Label statusLabel;

    @FXML
    private Label databaseLabel;

    @FXML
    private Label versionLabel;

    @FXML
    public void initialize() {

        statusLabel.setText("Ready");

        databaseLabel.setText("SQLite Connected");

        versionLabel.setText("Version " + applicationVersion);

    }

}