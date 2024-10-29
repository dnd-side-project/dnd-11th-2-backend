package com.dnd.runus.application.scale;

import com.dnd.runus.application.running.event.RunningRecordAddedEvent;
import com.dnd.runus.domain.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Component
@RequiredArgsConstructor
public class ScaleEventHandler {

    private final ScaleService scaleService;

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void handleScaleEvent(RunningRecordAddedEvent runningRecordAddedEvent) {
        Member member = runningRecordAddedEvent.member();
        scaleService.saveScaleAchievements(member);
    }
}
