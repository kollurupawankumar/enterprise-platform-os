package com.society.user.context;

import com.society.user.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserContext {

    private UserEntity currentUser;

    public synchronized void setCurrentUser(UserEntity user) {
        this.currentUser = user;
    }

    public synchronized UserEntity getCurrentUser() {
        return currentUser;
    }

    public synchronized void clear() {
        this.currentUser = null;
    }

    public synchronized boolean isLoggedIn() {
        return currentUser != null;
    }

    public synchronized boolean hasRole(String role) {
        return currentUser != null && currentUser.getRole().equalsIgnoreCase(role);
    }
}
