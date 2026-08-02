package com.society.common.navigation;


import org.springframework.stereotype.Component;

@Component
public class NavigationState {

    private View currentView = View.DASHBOARD;

    public View getCurrentView() {
        return currentView;
    }

    public void setCurrentView(View currentView) {
        this.currentView = currentView;
    }

    public boolean isCurrent(View view) {
        return currentView == view;
    }

}