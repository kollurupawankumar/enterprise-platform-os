package com.society.common.controller;

import com.society.common.navigation.NavigationManager;

public abstract class BaseController {

    protected final NavigationManager navigationManager;

    protected BaseController(NavigationManager navigationManager) {
        this.navigationManager = navigationManager;
    }

}