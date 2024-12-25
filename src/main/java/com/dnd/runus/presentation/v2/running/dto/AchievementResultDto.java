package com.dnd.runus.presentation.v2.running.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.lang.Nullable;

/**
 * 챌린지, 목표 달성 결과 통합 DTO
 */
public record AchievementResultDto(
    @Schema(description = "챌린지 이름 또는 설정된 목표 제목", examples = {"오늘 30분 동안 뛰기", "2.5km 달성"})
    String title,
    @Schema(description = "결과 문구", examples = {"정말 대단해요! 잘하셨어요", "아쉬워요. 내일 다시 도전해보세요!"})
    String subTitle,
    @Schema(description = "아이콘 이미지 URL")
    String iconUrl,
    @Schema(description = "성공 여부")
    boolean isSuccess,
    @Nullable
    @Schema(description = "퍼센테이지 값, V2 이전에 퍼센테이지를 나타낼 수 없는 챌린지일 경우 null값을 리턴합니다.")
    Double percentage
) {

}
