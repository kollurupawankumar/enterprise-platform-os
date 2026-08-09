package com.society.user.controller;

import com.society.common.controller.BaseController;
import com.society.common.navigation.NavigationManager;
import com.society.common.navigation.View;
import com.society.user.context.UserContext;
import com.society.user.entity.UserEntity;
import com.society.user.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class LoginController extends BaseController {

    private final UserService userService;
    private final UserContext userContext;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    public LoginController(
            NavigationManager navigationManager,
            UserService userService,
            UserContext userContext) {
        super(navigationManager);
        this.userService = userService;
        this.userContext = userContext;
    }

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password.");
            return;
        }

        Optional<UserEntity> authenticatedUser = userService.authenticate(username, password);

        if (authenticatedUser.isPresent()) {
            userContext.setCurrentUser(authenticatedUser.get());
            navigationManager.navigate(View.DASHBOARD);
        } else {
            showError("Invalid username or password, or account is disabled.");
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
