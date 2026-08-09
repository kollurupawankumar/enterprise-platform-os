package com.society.app;

import javafx.fxml.FXMLLoader;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.net.URL;

@Component
public class FXMLLoaderFactory {

    private final ApplicationContext applicationContext;

    public FXMLLoaderFactory(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public FXMLLoader create(String fxmlPath) {

        URL resource =
                FXMLLoaderFactory.class.getResource(fxmlPath);

        if (resource == null) {
            throw new IllegalArgumentException(
                    "FXML not found: " + fxmlPath);
        }

        FXMLLoader loader = new FXMLLoader(resource);

        loader.setControllerFactory(applicationContext::getBean);

        return loader;
    }

}