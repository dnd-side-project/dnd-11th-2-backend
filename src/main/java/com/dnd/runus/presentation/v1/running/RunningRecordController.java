package com.dnd.runus.presentation.v1.running;

import com.dnd.runus.application.running.RunningRecordService;
import com.dnd.runus.global.exception.type.ApiErrorType;
import com.dnd.runus.global.exception.type.ErrorType;
import com.dnd.runus.presentation.annotation.MemberId;
import com.dnd.runus.presentation.v1.running.dto.request.RunningRecordRequest;
import com.dnd.runus.presentation.v1.running.dto.response.RunningRecordAddResultResponse;
import com.dnd.runus.presentation.v1.running.dto.response.RunningRecordMonthlyDatesResponse;
import com.dnd.runus.presentation.v1.running.dto.response.RunningRecordSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "러닝 기록")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/running-records")
public class RunningRecordController {
    private final RunningRecordService runningRecordService;

    @GetMapping("monthly-dates")
    @Operation(summary = "해당 월의 러닝 기록 조회", description = "해당 월의 러닝 기록을 조회합니다. 해당 월에 러닝 기록이 있는 날짜를 반환합니다.")
    public RunningRecordMonthlyDatesResponse getRunningRecordDates(
            @MemberId long memberId, @RequestParam int year, @RequestParam int month) {
        List<LocalDate> days = runningRecordService.getRunningRecordDates(memberId, year, month);
        return new RunningRecordMonthlyDatesResponse(days);
    }

    @GetMapping("daily")
    @Operation(summary = "해당 일자의 러닝 기록 요약 조회", description = "해당 일자의 러닝 기록을 조회합니다. 해당 일자의 러닝 기록들을 반환합니다.")
    public RunningRecordSummaryResponse getRunningRecordSummaries(
            @MemberId long memberId, @RequestParam LocalDate date) {
        return runningRecordService.getRunningRecordSummaries(memberId, date);
    }

    @Operation(
            summary = "러닝 기록 추가 API",
            description = "러닝 기록을 추가합니다.<br>"
                    + "러닝 기록은 시작 시간, 종료 시간, 이모지, 챌린지 ID, 러닝 데이터로 구성됩니다. <br> "
                    + "러닝 데이터는 route(코스), 위치, 거리, 시간, 칼로리, 평균 페이스로 구성됩니다. <br> "
                    + "route는 최소 2개의 좌표를 가져야 합니다. <br> "
                    + "러닝 기록 추가에 성공하면 러닝 기록 ID, 기록 정보와 사용자 닉네임, 프로필 url을 반환합니다. <br>")
    @ApiErrorType({ErrorType.START_AFTER_END, ErrorType.ROUTE_MUST_HAVE_AT_LEAST_TWO_COORDINATES})
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public RunningRecordAddResultResponse addRunningRecord(
            @MemberId long memberId, @Valid @RequestBody RunningRecordRequest request) {
        return runningRecordService.addRunningRecord(memberId, request);
    }
}
