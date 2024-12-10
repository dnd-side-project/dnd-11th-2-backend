package com.dnd.runus.presentation.v1.challenge;

import com.dnd.runus.application.challenge.ChallengeService;
import com.dnd.runus.domain.challenge.Challenge;
import com.dnd.runus.domain.challenge.ChallengeCondition;
import com.dnd.runus.domain.challenge.ChallengeType;
import com.dnd.runus.domain.challenge.ChallengeWithCondition;
import com.dnd.runus.domain.challenge.ComparisonType;
import com.dnd.runus.domain.challenge.GoalMetricType;
import com.dnd.runus.presentation.config.ControllerTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WithMockUser
@WebMvcTest(ChallengeController.class)
class ChallengeControllerTest extends ControllerTestHelper {

    @Autowired
    private ChallengeController challengecontroller;

    @MockBean
    private ChallengeService challengeService;

    private long memberId;

    private ChallengeWithCondition challenge1;
    private ChallengeWithCondition challenge2;

    @BeforeEach
    void setUp() {
        setUpMockMvc(challengecontroller);

        challenge1 = new ChallengeWithCondition(
                new Challenge(1L, "3km 달리기", 21 * 60, "imageUrl", true, ChallengeType.TODAY),
                List.of(new ChallengeCondition(
                        GoalMetricType.DISTANCE, ComparisonType.GREATER_THAN_OR_EQUAL_TO, 3000)));

        challenge2 = new ChallengeWithCondition(
                new Challenge(2L, "1시간 30분 달리기", 90 * 60, "imageUrl", true, ChallengeType.TODAY),
                List.of(new ChallengeCondition(GoalMetricType.TIME, ComparisonType.GREATER_THAN_OR_EQUAL_TO, 90 * 60)));

        memberId = 1;
    }

    @DisplayName("오늘의 챌린지 조회 응답 형식 확인: 반환 문구, 단위 등을 확인합니다.)")
    @Test
    void getChallenges() throws Exception {
        // given
        given(challengeService.getChallenges(memberId)).willReturn(List.of(challenge1, challenge2));

        // when
        ResultActions result = mvc.perform(get("/api/v1/challenges").param("memberId", String.valueOf(memberId)));

        // then
        result.andExpect(jsonPath("$.data[0].challengeId")
                        .value(challenge1.challenge().challengeId()))
                .andExpect(
                        jsonPath("$.data[0].title").value(challenge1.challenge().name()))
                .andExpect(jsonPath("$.data[0].expectedTime").value("21분"))
                .andExpect(jsonPath("$.data[0].type").value("distance"))
                .andExpect(jsonPath("$.data[0].goalDistance").value(3.0))
                .andExpect(jsonPath("$.data[0].goalTime").doesNotExist())
                .andExpect(jsonPath("$.data[1].challengeId")
                        .value(challenge2.challenge().challengeId()))
                .andExpect(
                        jsonPath("$.data[1].title").value(challenge2.challenge().name()))
                .andExpect(jsonPath("$.data[1].expectedTime").value("1시간 30분"))
                .andExpect(jsonPath("$.data[1].type").value("time"))
                .andExpect(jsonPath("$.data[1].goalDistance").doesNotExist())
                .andExpect(jsonPath("$.data[1].goalTime").value(5400));
    }
}
