package com.dnd.runus.application.running;

import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.member.MemberRepository;
import com.dnd.runus.domain.running.RunningRecord;
import com.dnd.runus.domain.running.RunningRecordRepository;
import com.dnd.runus.global.exception.BusinessException;
import com.dnd.runus.global.exception.NotFoundException;
import com.dnd.runus.global.exception.type.ErrorType;
import com.dnd.runus.presentation.v1.running.dto.request.RunningRecordRequest;
import com.dnd.runus.presentation.v1.running.dto.response.RunningRecordReportResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;

@Service
public class RunningRecordService {
    private final RunningRecordRepository runningRecordRepository;
    private final MemberRepository memberRepository;
    private final ZoneOffset defaultZoneOffset;

    public RunningRecordService(
            RunningRecordRepository runningRecordRepository,
            MemberRepository memberRepository,
            @Value("${app.default-zone-offset}") ZoneOffset defaultZoneOffset) {
        this.runningRecordRepository = runningRecordRepository;
        this.memberRepository = memberRepository;
        this.defaultZoneOffset = defaultZoneOffset;
    }

    @Transactional
    public RunningRecordReportResponse addRunningRecord(long memberId, RunningRecordRequest request) {
        if (request.startAt().isAfter(request.endAt())) {
            throw new BusinessException(ErrorType.START_AFTER_END, request.startAt() + ", " + request.endAt());
        }
        if (request.runningData().route().size() < 2) {
            throw new BusinessException(
                    ErrorType.ROUTE_MUST_HAVE_AT_LEAST_TWO_COORDINATES,
                    request.runningData().route().toString());
        }
        // FIXME: badge에 left join해서 같이 조회하기
        Member member =
                memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(Member.class, memberId));
        // TODO: 챌린지 기능 추가 후 수정

        RunningRecord record = runningRecordRepository.save(RunningRecord.builder()
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
                .build());

        return RunningRecordReportResponse.from(record);
    }
}
