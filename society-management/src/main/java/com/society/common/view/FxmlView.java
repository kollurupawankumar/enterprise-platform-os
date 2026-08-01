package com.society.common.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.util.Objects;

public final class FxmlView implements View {

    private final String fxml;

    public FxmlView(String fxml) {
        this.fxml = fxml;
    }

    @Override
    public Parent load() {

        try {

            FXMLLoader loader = new FXMLLoader(
                    Objects.requireNonNull(
                            getClass().getResource(fxml)));

            return loader.load();

        } catch (IOException ex) {

            throw new RuntimeException(
                    "Unable to load " + fxml,
                    ex);

        }

    }

}