package com.society.app;

import com.society.config.SocietyConfig;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainController {

    @FXML
    private Label societyNameLabel;

    @FXML
    private Label versionLabel;

    @FXML
    public void initialize() {

        societyNameLabel.setText(
                SocietyConfig.societyName());

        versionLabel.setText(
                "Version "
                        + SocietyConfig.appVersion());

    }

}