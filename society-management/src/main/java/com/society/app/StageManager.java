package com.society.app;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class StageManager {

    private static final String MAIN_VIEW =
            "/fxml/main.fxml";

    private final FXMLLoaderFactory loaderFactory;

    public StageManager(FXMLLoaderFactory loaderFactory) {
        this.loaderFactory = loaderFactory;
    }

    public void showPrimaryStage(Stage stage) {

        try {

            Parent root =
                    loaderFactory.create(MAIN_VIEW).load();

            Scene scene = new Scene(root);

            scene.getStylesheets().add(
                    getClass()
                            .getResource("/css/application.css")
                            .toExternalForm());

            stage.setTitle("Society Office OS");

            stage.setMinWidth(1300);

            stage.setMinHeight(800);

            stage.setScene(scene);

            stage.centerOnScreen();

            stage.show();

        } catch (IOException ex) {

            throw new IllegalStateException(
                    "Unable to start application.",
                    ex);

        }

    }

}