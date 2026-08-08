package com.society.common.controller;


import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.springframework.stereotype.Component;

@Component
public class HomeController {

    @FXML
    private Label titleLabel;

    @FXML
    public void initialize() {
        titleLabel.setText("Society Office OS");
    }
}