package com.dnd.runus.global.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RunningResultComment {
    SUCCESS("정말 대단해요! 잘하셨어요"),
    FAILURE("아쉬워요. 내일 다시 도전해보세요!");

    private final String comment;
}
