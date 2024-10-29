package com.dnd.runus.application.member;

import com.dnd.runus.application.member.event.SignupEvent;
import com.dnd.runus.application.member.event.WithdrawEvent;
import com.dnd.runus.application.running.event.RunningRecordAddedEvent;
import com.dnd.runus.domain.running.RunningRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Component
@RequiredArgsConstructor
public class MemberEventHandler {

    private final MemberService memberService;
    private final MemberWithdrawService memberWithdrawService;

    @EventListener
    public void handleSignupEvent(SignupEvent signupEvent) {
        memberService.initMember(signupEvent.member());
    }

    @Async
    @EventListener
    public void handleWithdrawEvent(WithdrawEvent withdrawEvent) {
        memberWithdrawService.deleteAllDataAboutMember(withdrawEvent.memberId());
    }

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void handleRunningAddEvent(RunningRecordAddedEvent runningRecordAddedEvent) {
        RunningRecord runningRecord = runningRecordAddedEvent.runningRecord();

        memberService.addExp(runningRecordAddedEvent.member().memberId(), runningRecord.distanceMeter());
    }
}
