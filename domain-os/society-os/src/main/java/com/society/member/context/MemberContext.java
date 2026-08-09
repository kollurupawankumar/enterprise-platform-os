package com.society.member.context;

import com.society.member.dto.MemberDto;
import org.springframework.stereotype.Component;

@Component
public class MemberContext {

    private MemberDto selectedMember;

    public MemberDto getSelectedMember() {
        return selectedMember;
    }

    public void setSelectedMember(MemberDto selectedMember) {
        this.selectedMember = selectedMember;
    }

    public void clearSelectedMember() {
        this.selectedMember = null;
    }

    public boolean isEditMode() {
        return selectedMember != null;
    }
}
