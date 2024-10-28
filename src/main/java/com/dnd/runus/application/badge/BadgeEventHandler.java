package com.dnd.runus.application.badge;

import com.dnd.runus.application.member.event.SignupEvent;
import com.dnd.runus.global.constant.BadgeType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BadgeEventHandler {
    private final BadgeService badgeService;

    @EventListener
    public void handleSignupEvent(SignupEvent signupEvent) {
        badgeService.achieveBadge(signupEvent.member(), BadgeType.PERSONAL_RECORD, 0);
    }
}
