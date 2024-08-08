package com.dnd.runus.application.running;

import com.dnd.runus.domain.common.Coordinate;
import com.dnd.runus.domain.common.Pace;
import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.member.MemberRepository;
import com.dnd.runus.domain.running.RunningRecord;
import com.dnd.runus.domain.running.RunningRecordRepository;
import com.dnd.runus.global.constant.MemberRole;
import com.dnd.runus.global.constant.RunningEmoji;
import com.dnd.runus.global.exception.BusinessException;
import com.dnd.runus.global.exception.type.ErrorType;
import com.dnd.runus.presentation.v1.running.dto.RunningRecordMetricsDto;
import com.dnd.runus.presentation.v1.running.dto.request.RunningRecordRequest;
import com.dnd.runus.presentation.v1.running.dto.response.RunningRecordReportResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class RunningRecordServiceTest {
    private RunningRecordService runningRecordService;

    @Mock
    private RunningRecordRepository runningRecordRepository;

    @Mock
    private MemberRepository memberRepository;

    private final ZoneOffset defaultZoneOffset = ZoneOffset.of("+9");

    @BeforeEach
    void setUp() {
        runningRecordService = new RunningRecordService(runningRecordRepository, memberRepository, defaultZoneOffset);
    }

    @Test
    @DisplayName("올바른 러닝 기록 추가 요청시, 정상적으로 러닝 기록이 추가된다.")
    void addRunningRecord() {
        // given
        RunningRecordRequest request = new RunningRecordRequest(
                LocalDateTime.of(2021, 1, 1, 12, 10, 30),
                LocalDateTime.of(2021, 1, 1, 13, 12, 10),
                RunningEmoji.VERY_GOOD,
                1L,
                new RunningRecordMetricsDto(
                        new Pace(5, 30),
                        "location name",
                        Duration.ofSeconds(10_100),
                        10_000,
                        500.0,
                        List.of(new Coordinate(128.0, 37.0), new Coordinate(128.1, 37.1))));

        Member member = new Member(MemberRole.USER, "nickname1");
        RunningRecord expected = RunningRecord.builder()
                .member(member)
                .startAt(request.startAt().atOffset(defaultZoneOffset))
                .endAt(request.endAt().atOffset(defaultZoneOffset))
                .route(request.runningData().route())
                .emoji(request.emoji())
                .location(request.runningData().location())
                .distanceMeter(request.runningData().distanceMeter())
                .duration(request.runningData().runningTime())
                .calorie(request.runningData().calorie())
                .averagePace(request.runningData().averagePace())
                .build();
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(runningRecordRepository.save(expected)).willReturn(expected);

        // when
        RunningRecordReportResponse response = runningRecordService.addRunningRecord(1L, request);

        // then
        assertEquals(request.startAt(), response.startAt());
        assertEquals(request.endAt(), response.endAt());
    }

    @Test
    @DisplayName("시작 시간이 종료 시간보다 늦을 경우, BusinessException이 발생한다.")
    void addRunningRecord_StartAtAfterEndAt() {
        // given
        RunningRecordRequest request = new RunningRecordRequest(
                LocalDateTime.of(2021, 1, 1, 12, 10, 30),
                LocalDateTime.of(2021, 1, 1, 11, 12, 10),
                RunningEmoji.VERY_GOOD,
                1L,
                new RunningRecordMetricsDto(
                        new Pace(5, 30),
                        "location name",
                        Duration.ofSeconds(10_100),
                        10_000,
                        500.0,
                        List.of(new Coordinate(128.0, 37.0), new Coordinate(128.1, 37.1))));
        // when
        BusinessException exception =
                assertThrows(BusinessException.class, () -> runningRecordService.addRunningRecord(1L, request));
        // then
        assertEquals(ErrorType.START_AFTER_END, exception.getType());
    }

    @Test
    @DisplayName("경로의 좌표가 2개 미만일 경우, BusinessException이 발생한다.")
    void addRunningRecord_RouteMustHaveAtLeastTwoCoordinates() {
        // given
        RunningRecordRequest request = new RunningRecordRequest(
                LocalDateTime.of(2021, 1, 1, 12, 10, 30),
                LocalDateTime.of(2021, 1, 1, 13, 12, 10),
                RunningEmoji.VERY_GOOD,
                1L,
                new RunningRecordMetricsDto(
                        new Pace(5, 30),
                        "location name",
                        Duration.ofSeconds(10_100),
                        10_000,
                        500.0,
                        List.of(new Coordinate(128.0, 37.0))));

        // when
        BusinessException exception =
                assertThrows(BusinessException.class, () -> runningRecordService.addRunningRecord(1L, request));
        // then
        assertEquals(ErrorType.ROUTE_MUST_HAVE_AT_LEAST_TWO_COORDINATES, exception.getType());
    }
}
