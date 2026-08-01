package com.society.common.navigation;

import com.society.app.FXMLLoaderFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class NavigationManager {

    private final FXMLLoaderFactory loaderFactory;

    private StackPane contentHost;

    public NavigationManager(FXMLLoaderFactory loaderFactory) {
        this.loaderFactory = loaderFactory;
    }

    public void setContentHost(StackPane contentHost) {
        this.contentHost = contentHost;
    }

    public void navigate(View view) {

        if (contentHost == null) {
            throw new IllegalStateException(
                    "Content host has not been initialized.");
        }

        try {

            FXMLLoader loader =
                    loaderFactory.create(view.getFxmlPath());

            Node content = loader.load();

            contentHost.getChildren().setAll(content);

        } catch (IOException ex) {

            throw new IllegalStateException(
                    "Unable to load view: " + view,
                    ex);

        }

    }

}