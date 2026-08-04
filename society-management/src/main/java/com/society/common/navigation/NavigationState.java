package com.society.common.navigation;

import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Component
public class NavigationState {

    private View currentView = View.DASHBOARD;
    private final List<Consumer<View>> listeners = new ArrayList<>();

    public View getCurrentView() {
        return currentView;
    }

    public void setCurrentView(View currentView) {
        this.currentView = currentView;
        for (Consumer<View> listener : listeners) {
            listener.accept(currentView);
        }
    }

    public boolean isCurrent(View view) {
        return currentView == view;
    }

    public void addListener(Consumer<View> listener) {
        this.listeners.add(listener);
    }
}