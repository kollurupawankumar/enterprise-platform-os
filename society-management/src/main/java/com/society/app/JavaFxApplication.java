package com.society.app;

import com.society.SocietyApplication;
import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class JavaFxApplication extends Application {

    private ConfigurableApplicationContext applicationContext;

    @Override
    public void init() {

        applicationContext =
                new SpringApplicationBuilder(SocietyApplication.class)
                        .headless(false)
                        .run(getParameters().getRaw().toArray(new String[0]));

    }

    @Override
    public void start(Stage primaryStage) {

        StageManager stageManager =
                applicationContext.getBean(StageManager.class);

        stageManager.showPrimaryStage(primaryStage);

    }

    @Override
    public void stop() {

        applicationContext.close();

        javafx.application.Platform.exit();

    }

}