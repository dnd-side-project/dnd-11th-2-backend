package com.dnd.runus.application.member;

import com.dnd.runus.application.member.event.SignupEvent;
import com.dnd.runus.application.member.event.WithdrawEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberEventHandler {

    private final MemberService memberService;
    private final MemberWithdrawService memberWithdrawService;

    public void handleSignupEvent(SignupEvent signupEvent) {
        memberService.initMember(signupEvent.member());
    }

    public void handleWithdrawEvent(WithdrawEvent withdrawEvent) {
        memberWithdrawService.deleteAllDataAboutMember(withdrawEvent.memberId());
    }
}
