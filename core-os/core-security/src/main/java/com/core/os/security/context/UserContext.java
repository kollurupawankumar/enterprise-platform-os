package com.core.os.security.context;

import org.springframework.stereotype.Component;

@Component("coreUserContext")
public class UserContext {
    private String currentUsername = "admin";
    private String currentRole = "ADMINISTRATOR";

    public String getCurrentUsername() { return currentUsername; }
    public void setCurrentUsername(String username) { this.currentUsername = username; }

    public String getCurrentRole() { return currentRole; }
    public void setCurrentRole(String role) { this.currentRole = role; }

    public boolean canEdit(String moduleName) {
        if ("ADMINISTRATOR".equalsIgnoreCase(currentRole) || "PRESIDENT".equalsIgnoreCase(currentRole) || "SECRETARY".equalsIgnoreCase(currentRole) || "TREASURER".equalsIgnoreCase(currentRole)) {
            return true;
        }
        return false;
    }
}
