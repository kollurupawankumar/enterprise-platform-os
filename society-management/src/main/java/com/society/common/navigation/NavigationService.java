package com.society.common.navigation;

import com.society.common.view.View;
import javafx.scene.layout.StackPane;

public final class NavigationService {

    private final StackPane contentPane;

    public NavigationService(StackPane contentPane) {

        this.contentPane = contentPane;

    }

    public void navigate(View view) {

        contentPane.getChildren().setAll(view.load());

    }

}