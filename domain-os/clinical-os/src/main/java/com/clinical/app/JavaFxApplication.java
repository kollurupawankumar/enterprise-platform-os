package com.clinical.app;

import com.clinical.ClinicalOSApplication;
import javafx.application.Application;
import javafx.application.Platform;
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
        log.info("Initializing Spring Boot application context for Clinical OS...");
        applicationContext = new SpringApplicationBuilder(ClinicalOSApplication.class)
                .headless(false)
                .run(getParameters().getRaw().toArray(new String[0]));
        log.info("Spring Boot application context initialized successfully.");
    }

    @Override
    public void start(Stage primaryStage) {
        log.info("Starting JavaFX Primary Stage for Clinical OS...");
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxml/main_clinical_app.fxml"));
            loader.setControllerFactory(applicationContext::getBean);
            javafx.scene.Parent root = loader.load();
            primaryStage.setTitle("ClinicalOS - Enterprise Hospital Platform");
            primaryStage.setScene(new javafx.scene.Scene(root, 1280, 800));
            primaryStage.show();
        } catch (Exception e) {
            log.error("Failed to start ClinicalOS GUI stage", e);
        }
    }

    @Override
    public void stop() {
        log.info("Shutting down Clinical OS desktop application...");
        if (applicationContext != null) {
            applicationContext.close();
        }
        Platform.exit();
    }
}
