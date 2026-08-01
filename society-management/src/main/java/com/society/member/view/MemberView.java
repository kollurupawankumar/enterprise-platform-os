package com.society.member.view;

import com.society.common.view.FxmlView;
import com.society.common.view.View;

public final class MemberView {

    public static final View VIEW =
            new FxmlView("/fxml/member/Member.fxml");

    private MemberView() {
    }

}