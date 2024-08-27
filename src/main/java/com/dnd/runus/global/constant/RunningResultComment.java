package com.dnd.runus.global.constant;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum RunningResultComment {
    SUCCESS_COMMENT("정말 대단해요! 잘하셨어요"),
    FAILURE_COMMENT("아쉬워요. 내일 다시 도전해보세요!");

    private final String comment;

    // TODO(subtitle(comment) 리턴, 챌린지 성취, 목표 성취 값 리턴 할 때 사용)
    public static String getComment(boolean successStatus) {
        return successStatus ? SUCCESS_COMMENT.comment : FAILURE_COMMENT.comment;
    }
}
