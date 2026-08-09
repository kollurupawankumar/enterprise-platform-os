package com.society.app;

import com.society.SocietyApplication;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class JavaFxApplication extends Application {

    private static final Logger log = LoggerFactory.getLogger(JavaFxApplication.class);
    private ConfigurableApplicationContext applicationContext;

    @Override
    public void init() {
        log.info("Initializing Spring Boot application context for Society Office OS...");
        
        // Setup Global JavaFX Thread Exception Handler
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            log.error("Unhandled Exception on Thread [{}]: {}", thread.getName(), throwable.getMessage(), throwable);
            Platform.runLater(() -> showErrorDialog("System Exception", throwable.getMessage() != null ? throwable.getMessage() : throwable.toString()));
        });

        applicationContext =
                new SpringApplicationBuilder(SocietyApplication.class)
                        .headless(false)
                        .run(getParameters().getRaw().toArray(new String[0]));
        log.info("Spring Boot application context initialized successfully.");
    }

    @Override
    public void start(Stage primaryStage) {
        log.info("Starting JavaFX Primary Stage...");
        StageManager stageManager =
                applicationContext.getBean(StageManager.class);

        stageManager.showPrimaryStage(primaryStage);
    }

    @Override
    public void stop() {
        log.info("Shutting down Society Office OS desktop application...");
        if (applicationContext != null) {
            applicationContext.close();
        }
        Platform.exit();
        log.info("Application shutdown complete.");
    }

    private static void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText("An unexpected system error occurred");
        alert.setContentText(message);
        alert.showAndWait();
    }

}