package com.dnd.runus.presentation.v2.running;

import com.dnd.runus.application.running.RunningRecordService;
import com.dnd.runus.application.running.RunningRecordServiceV2;
import com.dnd.runus.global.exception.type.ApiErrorType;
import com.dnd.runus.global.exception.type.ErrorType;
import com.dnd.runus.presentation.annotation.MemberId;
import com.dnd.runus.presentation.v1.running.dto.response.RunningRecordMonthlySummaryResponse;
import com.dnd.runus.presentation.v2.running.dto.request.RunningRecordRequestV2;
import com.dnd.runus.presentation.v2.running.dto.response.RunningRecordMonthlySummaryResponseV2;
import com.dnd.runus.presentation.v2.running.dto.response.RunningRecordResultResponseV2;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/running-records")
public class RunningRecordControllerV2 {
    private final RunningRecordServiceV2 runningRecordService2;
    private final RunningRecordService runningRecordService;

    @Operation(summary = "이번 달 러닝 기록 조회(홈화면) V2", description = """
    홈화면의 이번 달 러닝 기록을 조회 합니다.<br>
    """)
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("monthly-summary")
    public RunningRecordMonthlySummaryResponseV2 getMonthlyRunningSummary(@MemberId long memberId) {
        RunningRecordMonthlySummaryResponse monthlyRunningSummery =
                runningRecordService.getMonthlyRunningSummery(memberId);
        return new RunningRecordMonthlySummaryResponseV2(
                monthlyRunningSummery.month(),
                monthlyRunningSummery.monthlyTotalMeter(),
                runningRecordService2.getPercentageValues(memberId));
    }

    @Operation(
            summary = "러닝 기록 추가 API V2",
            description =
                    """
            러닝 기록을 추가합니다.<br>
            러닝 기록은 시작 시간, 종료 시간, 러닝 평가(emotion), 러닝 데이터 등으로 구성됩니다. <br>
            챌린지 모드가 normal : challengeValues, goalValues 둘다 null <br>
            챌린지 모드가 challenge : challengeValues 필수 값 <br>
            챌린지 모드가 goal : goalValues 필수 값 <br>
            러닝 데이터는 위치, 거리, 시간, 칼로리, 평균 페이스, 러닝 경로로 구성됩니다. <br>
            러닝 기록 추가에 성공하면 러닝 기록 ID, 기록 정보를 반환합니다. <br>
            """)
    @ApiErrorType({
        ErrorType.START_AFTER_END,
        ErrorType.CHALLENGE_VALUES_REQUIRED_IN_CHALLENGE_MODE,
        ErrorType.GOAL_VALUES_REQUIRED_IN_GOAL_MODE,
        ErrorType.GOAL_TIME_AND_DISTANCE_BOTH_EXIST,
        ErrorType.ROUTE_MUST_HAVE_AT_LEAST_TWO_COORDINATES,
        ErrorType.CHALLENGE_NOT_ACTIVE
    })
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public RunningRecordResultResponseV2 addRunningRecord(
            @MemberId long memberId, @Valid @RequestBody RunningRecordRequestV2 request) {
        return RunningRecordResultResponseV2.from(runningRecordService.addRunningRecordV2(memberId, request));
    }
}
