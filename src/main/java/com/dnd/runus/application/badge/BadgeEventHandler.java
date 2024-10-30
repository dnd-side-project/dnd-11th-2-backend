package com.dnd.runus.application.badge;

import com.dnd.runus.application.member.event.SignupEvent;
import com.dnd.runus.application.running.event.RunningRecordAddedEvent;
import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.running.RunningRecord;
import com.dnd.runus.global.constant.BadgeType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Component
@RequiredArgsConstructor
public class BadgeEventHandler {
    private final BadgeService badgeService;

    @EventListener
    public void handleSignupEvent(SignupEvent signupEvent) {
        badgeService.achieveBadge(signupEvent.member(), BadgeType.PERSONAL_RECORD, 0);
    }

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void handleRunningRecordAddedEvent(RunningRecordAddedEvent event) {
        Member member = event.member();
        RunningRecord runningRecord = event.runningRecord();

        badgeService.achieveBadge(member, BadgeType.PERSONAL_RECORD, runningRecord.distanceMeter());

        badgeService.achieveBadge(member, BadgeType.DISTANCE_METER, event.totalDistanceMeter());
        badgeService.achieveBadge(member, BadgeType.DURATION_SECONDS, event.totalRunningSeconds());
    }
}
