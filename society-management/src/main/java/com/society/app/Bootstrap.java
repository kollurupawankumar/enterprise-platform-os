package com.society.app;

import com.society.config.SocietyConfig;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class Bootstrap {

    private Bootstrap() {
    }

    public static void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
                Bootstrap.class.getResource("/fxml/main/ApplicationLayout.fxml"));

        Scene scene = new Scene(loader.load(), 1200, 700);

        scene.getStylesheets().add(
                Bootstrap.class.getResource("/css/application.css")
                        .toExternalForm());

        stage.setTitle(SocietyConfig.appName());

        stage.setScene(scene);

        stage.setMinWidth(1000);
        stage.setMinHeight(650);

        stage.centerOnScreen();

        stage.show();

    }

}