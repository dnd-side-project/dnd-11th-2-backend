package com.dnd.runus.presentation.v2.running;

import com.dnd.runus.application.running.RunningRecordService;
import com.dnd.runus.application.running.RunningRecordServiceV2;
import com.dnd.runus.application.running.dto.RunningResultDto;
import com.dnd.runus.domain.common.CoordinatePoint;
import com.dnd.runus.domain.common.Pace;
import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.running.RunningRecord;
import com.dnd.runus.global.constant.MemberRole;
import com.dnd.runus.presentation.config.ControllerTestHelper;
import com.dnd.runus.presentation.v2.running.dto.request.RunningRecordRequestV2;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
@WebMvcTest(RunningRecordControllerV2.class)
class RunningRecordControllerV2Test extends ControllerTestHelper {

    @Autowired
    private RunningRecordControllerV2 runningRecordControllerV2;

    @MockBean
    private RunningRecordService runningRecordService;

    @MockBean
    private RunningRecordServiceV2 runningRecordServicev2;

    @Autowired
    private ObjectMapper mapper;

    private long memberId;
    private final ZoneOffset defaultZoneOffset = ZoneOffset.of("+9");

    @BeforeEach
    void setUp() {
        setUpMockMvc(runningRecordControllerV2);
        memberId = 1;
    }

    @Test
    @DisplayName("러닝 결과 추가에 대한 응답형식 확인")
    void addRunningRecord_Normal_CheckRunningPath() throws Exception {
        // given
        String requestJson =
                """
            {
              "startAt" : "2024-12-23 17:49:36",
              "endAt" : "2024-12-23 20:49:36",
              "startLocation" : "서울시 강남구",
              "endLocation" : "서울시 송파구",
              "emotion" : "very-good",
              "achievementMode" : "normal",
              "runningData" : {
                "runningTime" : "00:05:30",
                "distanceMeter" : 1000,
                "calorie" : 100.0,
                "route" : [ {
                  "start" : {
                    "longitude" : 1.0,
                    "latitude" : 1.0
                  },
                  "end" : {
                    "longitude" : 2.0,
                    "latitude" : 2.0
                  }
                }, {
                  "start" : {
                    "longitude" : 3.0,
                    "latitude" : 3.0
                  },
                  "end" : {
                    "longitude" : 4.0,
                    "latitude" : 4.0
                  }
                }, {
                  "start" : {
                    "longitude" : 5.0,
                    "latitude" : 5.0
                  },
                  "end" : {
                    "longitude" : 6.0,
                    "latitude" : 6.0
                  }
                } ]
              }
            }
            """;
        RunningRecordRequestV2 request = mapper.readValue(requestJson, RunningRecordRequestV2.class);
        given(runningRecordService.addRunningRecordV2(memberId, request))
                .willReturn(new RunningResultDto(
                        createRunningRecordFrom(request),
                        com.dnd.runus.presentation.v1.running.dto.request.RunningAchievementMode.NORMAL,
                        null,
                        null,
                        null));

        // when
        ResultActions result = mvc.perform(post("/api/v2/running-records")
                .param("memberId", String.valueOf(memberId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.emotion").value("very-good"))
                .andExpect(jsonPath("$.data.achievementMode").value("normal"))
                .andExpect(jsonPath("$.data.challenge").doesNotExist())
                .andExpect(jsonPath("$.data.goal").doesNotExist())
                .andExpect(jsonPath("$.data.runningData.averagePace").value("5’30”"))
                .andExpect(
                        jsonPath("$.data.runningData.route[0].start.longitude").value("1.0"))
                .andExpect(jsonPath("$.data.runningData.route[0].end.longitude").value("2.0"))
                .andExpect(
                        jsonPath("$.data.runningData.route[1].start.longitude").value("3.0"))
                .andExpect(jsonPath("$.data.runningData.route[1].end.longitude").value("4.0"))
                .andExpect(
                        jsonPath("$.data.runningData.route[2].start.longitude").value("5.0"))
                .andExpect(jsonPath("$.data.runningData.route[2].end.longitude").value("6.0"));
    }

    private RunningRecord createRunningRecordFrom(RunningRecordRequestV2 request) {
        return RunningRecord.builder()
                .member(new Member(memberId, MemberRole.USER, "nickname1", OffsetDateTime.now(), OffsetDateTime.now()))
                .startAt(request.startAt().atZone(defaultZoneOffset))
                .endAt(request.endAt().atZone(defaultZoneOffset))
                .emoji(request.emotion())
                .startLocation(request.startLocation())
                .endLocation(request.endLocation())
                .distanceMeter(request.runningData().distanceMeter())
                .duration(request.runningData().runningTime())
                .calorie(request.runningData().calorie())
                .averagePace(Pace.from(
                        request.runningData().distanceMeter(),
                        request.runningData().runningTime()))
                .route(request.runningData().route().stream()
                        .flatMap(point -> Stream.of(
                                new CoordinatePoint(
                                        point.start().longitude(), point.start().latitude()),
                                new CoordinatePoint(
                                        point.end().longitude(), point.end().latitude())))
                        .collect(Collectors.toList()))
                .build();
    }
}
