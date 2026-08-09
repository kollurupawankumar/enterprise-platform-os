package com.society.common.navigation;

import com.society.app.FXMLLoaderFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class NavigationManager {

    private static final Logger log = LoggerFactory.getLogger(NavigationManager.class);

    private final FXMLLoaderFactory loaderFactory;

    private StackPane contentHost;

    private final NavigationState navigationState;

    public NavigationManager(
            FXMLLoaderFactory loaderFactory,
            NavigationState navigationState) {

        this.loaderFactory = loaderFactory;
        this.navigationState = navigationState;
    }

    public void setContentHost(StackPane contentHost) {
        this.contentHost = contentHost;
    }

    public void navigate(View view) {

        if (contentHost == null) {
            log.error("Navigation failed: Content host has not been initialized.");
            throw new IllegalStateException(
                    "Content host has not been initialized.");
        }

        try {
            log.info("Navigating to view: {} ({})", view, view.getFxmlPath());

            FXMLLoader loader =
                    loaderFactory.create(view.getFxmlPath());

            Node content = loader.load();

            contentHost.getChildren().setAll(content);

            navigationState.setCurrentView(view);

        } catch (IOException ex) {
            log.error("Unable to load view: {} ({})", view, view.getFxmlPath(), ex);
            throw new IllegalStateException(
                    "Unable to load view: " + view,
                    ex);

        }

    }

}