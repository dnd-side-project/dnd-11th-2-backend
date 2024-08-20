package com.dnd.runus.application.running;

import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.member.MemberLevelRepository;
import com.dnd.runus.domain.member.MemberRepository;
import com.dnd.runus.domain.running.RunningRecord;
import com.dnd.runus.domain.running.RunningRecordRepository;
import com.dnd.runus.global.exception.BusinessException;
import com.dnd.runus.global.exception.NotFoundException;
import com.dnd.runus.global.exception.type.ErrorType;
import com.dnd.runus.presentation.v1.running.dto.request.RunningRecordRequest;
import com.dnd.runus.presentation.v1.running.dto.response.RunningRecordReportResponse;
import com.dnd.runus.presentation.v1.running.dto.response.RunningRecordSummaryResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static com.dnd.runus.global.constant.TimeConstant.SERVER_TIMEZONE;

@Service
public class RunningRecordService {
    private final RunningRecordRepository runningRecordRepository;
    private final MemberRepository memberRepository;
    private final MemberLevelRepository memberLevelRepository;
    private final ZoneOffset defaultZoneOffset;

    public RunningRecordService(
            RunningRecordRepository runningRecordRepository,
            MemberRepository memberRepository,
            MemberLevelRepository memberLevelRepository,
            @Value("${app.default-zone-offset}") ZoneOffset defaultZoneOffset) {
        this.runningRecordRepository = runningRecordRepository;
        this.memberRepository = memberRepository;
        this.memberLevelRepository = memberLevelRepository;
        this.defaultZoneOffset = defaultZoneOffset;
    }

    @Transactional(readOnly = true)
    public List<LocalDate> getRunningRecordDates(long memberId, int year, int month) {
        OffsetDateTime from = LocalDate.of(year, month, 1).atStartOfDay().atOffset((ZoneOffset.of(SERVER_TIMEZONE)));
        OffsetDateTime to = from.plusMonths(1);

        List<RunningRecord> records = runningRecordRepository.findByMemberIdAndStartAtBetween(memberId, from, to);

        return records.stream()
                .map(RunningRecord::startAt)
                .map(OffsetDateTime::toLocalDate)
                .distinct()
                .sorted()
                .toList();
    }

    public RunningRecordSummaryResponse getRunningRecordSummaries(long memberId, LocalDate date) {
        OffsetDateTime from = date.atStartOfDay().atOffset(ZoneOffset.of(SERVER_TIMEZONE));
        OffsetDateTime to = from.plusDays(1);

        List<RunningRecord> records = runningRecordRepository.findByMemberIdAndStartAtBetween(memberId, from, to);
        return RunningRecordSummaryResponse.from(records);
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
                .startLocation(request.startLocation())
                .endLocation(request.endLocation())
                .distanceMeter(request.runningData().distanceMeter())
                .duration(request.runningData().runningTime())
                .calorie(request.runningData().calorie())
                .averagePace(request.runningData().averagePace())
                .build());

        memberLevelRepository.updateMemberLevel(memberId, request.runningData().distanceMeter());

        return RunningRecordReportResponse.from(record);
    }
}
