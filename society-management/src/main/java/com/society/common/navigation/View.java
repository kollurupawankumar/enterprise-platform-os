package com.society.common.navigation;

public enum View {

    DASHBOARD("/fxml/dashboard/dashboard.fxml"),

    MEMBERS("/fxml/member/member.fxml"),

    SHARES("/fxml/share/share.fxml"),

    CERTIFICATES("/fxml/certificate/certificate.fxml"),

    REPORTS("/fxml/report/report.fxml"),

    ADMINISTRATION("/fxml/admin/administration.fxml");

    private final String fxmlPath;

    View(String fxmlPath) {
        this.fxmlPath = fxmlPath;
    }

    public String getFxmlPath() {
        return fxmlPath;
    }
}