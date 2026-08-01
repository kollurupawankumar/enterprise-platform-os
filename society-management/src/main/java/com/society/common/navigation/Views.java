package com.society.common.navigation;

import com.society.common.view.FxmlView;
import com.society.common.view.View;

public final class Views {

    private Views() {
    }

    public static final View DASHBOARD =
            new FxmlView("/fxml/dashboard/Dashboard.fxml");

    public static final View MEMBER =
            new FxmlView("/fxml/member/Member.fxml");

}