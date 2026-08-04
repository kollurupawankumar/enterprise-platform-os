package com.society.common.navigation;

public enum View {

    DASHBOARD("/fxml/dashboard/dashboard.fxml"),

    MEMBERS("/fxml/member/member.fxml"),

    SHARES("/fxml/share/share.fxml"),

    SHARE_ALLOTMENT("/fxml/share/share-allotment.fxml"),

    CERTIFICATES("/fxml/certificate/certificate.fxml"),

    MEMBER_REGISTRATION("/fxml/member/member-registration.fxml"),

    REPORTS("/fxml/report/report.fxml"),

    ADMINISTRATION("/fxml/admin/administration.fxml"),

    SOCIETY_SETUP("/fxml/society/society-setup.fxml"),

    LOGIN("/fxml/auth/login.fxml"),

    PROPERTIES("/fxml/property/property.fxml"),

    GOVERNANCE("/fxml/governance/governance.fxml"),

    OPERATIONS("/fxml/operations/operations.fxml"),

    FINANCE("/fxml/finance/finance.fxml"),

    SEARCH("/fxml/search/search.fxml"),

    DOCUMENTS("/fxml/document/document.fxml"),

    KNOWLEDGE_BASE("/fxml/knowledge/knowledge.fxml"),

    BUDGET_AUDIT("/fxml/finance/budget-audit.fxml");



    private final String fxmlPath;

    View(String fxmlPath) {
        this.fxmlPath = fxmlPath;
    }

    public String getFxmlPath() {
        return fxmlPath;
    }
}