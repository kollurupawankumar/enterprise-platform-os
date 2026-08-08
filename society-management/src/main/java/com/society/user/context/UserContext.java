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

    public synchronized boolean canAccess(String module) {
        if (currentUser == null) {
            return false;
        }

        String role = currentUser.getRole() != null ? currentUser.getRole().toUpperCase() : "";

        // ADMINISTRATOR / PRESIDENT / SECRETARY: Full access to all modules
        if ("ADMINISTRATOR".equals(role) || "ADMIN".equals(role) || "PRESIDENT".equals(role) || "SECRETARY".equals(role)) {
            return true;
        }

        return switch (module.toUpperCase()) {
            case "DASHBOARD" -> true;
            case "SEARCH", "UNIVERSAL_SEARCH" -> true;
            case "MEMBERS" -> "TREASURER".equals(role) || "OFFICE_ASSISTANT".equals(role) || "AUDITOR".equals(role);
            case "PROPERTIES" -> "TREASURER".equals(role) || "OFFICE_ASSISTANT".equals(role) || "AUDITOR".equals(role);
            case "GOVERNANCE" -> "AUDITOR".equals(role) || "OFFICE_ASSISTANT".equals(role);
            case "OPERATIONS" -> "TREASURER".equals(role) || "OFFICE_ASSISTANT".equals(role);
            case "FINANCE" -> "TREASURER".equals(role) || "AUDITOR".equals(role);
            case "SHARES" -> "TREASURER".equals(role) || "AUDITOR".equals(role);
            case "CERTIFICATES" -> "TREASURER".equals(role) || "OFFICE_ASSISTANT".equals(role);
            case "REPORTS" -> "TREASURER".equals(role) || "AUDITOR".equals(role) || "OFFICE_ASSISTANT".equals(role);
            case "DOCUMENTS" -> "TREASURER".equals(role) || "AUDITOR".equals(role) || "OFFICE_ASSISTANT".equals(role);
            case "KNOWLEDGE_BASE" -> true;
            case "BUDGET_AUDIT" -> "TREASURER".equals(role) || "AUDITOR".equals(role);
            case "ADMINISTRATION" -> false; // Only Admin / President / Secretary can access Administration
            default -> true;
        };
    }
}
