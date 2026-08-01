package com.society.app;

import com.society.common.navigation.NavigationService;
import com.society.common.navigation.Views;
import com.society.config.SocietyConfig;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class MainController {

    @FXML
    private Label societyNameLabel;

    @FXML
    private Label versionLabel;

    @FXML
    private StackPane contentPane;

    @FXML
    private Button dashboardBtn;

    @FXML
    private Button memberBtn;

    @FXML
    private Button shareBtn;

    @FXML
    private Button certificateBtn;

    @FXML
    private Button reportBtn;

    @FXML
    private Button adminBtn;

    private NavigationService navigation;

    @FXML
    public void initialize() {

        initializeLabels();

        initializeNavigation();

        registerEvents();

    }

    private void initializeLabels() {

        societyNameLabel.setText(
                SocietyConfig.societyName());

        versionLabel.setText(
                SocietyConfig.appVersion());

    }

    private void initializeNavigation() {

        navigation = new NavigationService(contentPane);

        navigation.navigate(Views.DASHBOARD);

    }

    private void registerEvents() {

        dashboardBtn.setOnAction(e ->
                navigation.navigate(Views.DASHBOARD));

        memberBtn.setOnAction(e ->
                navigation.navigate(Views.MEMBER));

        shareBtn.setDisable(true);

        certificateBtn.setDisable(true);

        reportBtn.setDisable(true);

        adminBtn.setDisable(true);

    }

}