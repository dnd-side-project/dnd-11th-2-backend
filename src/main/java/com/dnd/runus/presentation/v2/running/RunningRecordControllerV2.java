package com.dnd.runus.presentation.v2.running;

import com.dnd.runus.application.running.RunningRecordService;
import com.dnd.runus.application.running.RunningRecordServiceV2;
import com.dnd.runus.presentation.annotation.MemberId;
import com.dnd.runus.presentation.v1.running.dto.response.RunningRecordMonthlySummaryResponse;
import com.dnd.runus.presentation.v2.running.dto.response.RunningRecordMonthlySummaryResponseV2;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
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
}
