package com.society.header.controller;

import com.society.society.dto.SocietyDto;
import com.society.society.runtime.SocietyProvider;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.springframework.stereotype.Component;

@Component
public class HeaderController {

    private final SocietyProvider societyProvider;

    @FXML
    private Label societyNameLabel;

    @FXML
    private Label registrationNumberLabel;

    @FXML
    private Label financialYearLabel;

    public HeaderController(SocietyProvider societyProvider) {
        this.societyProvider = societyProvider;
    }

    @FXML
    public void initialize() {
        loadSociety();
    }

    public void refresh() {
        loadSociety();
    }

    private void loadSociety() {

        if (!societyProvider.hasSociety()) {

            societyNameLabel.setText("Society Management System");
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