package com.society;

import com.society.app.Bootstrap;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        Bootstrap.start(primaryStage);

    }

    public static void main(String[] args) {

        launch(args);

    }

}