package com.dnd.runus.application.goalAchievement;

import com.dnd.runus.domain.goalAchievement.GoalAchievement;
import com.dnd.runus.domain.goalAchievement.GoalAchievementRepository;
import com.dnd.runus.domain.goalAchievement.dto.GoalAchievementDto;
import com.dnd.runus.domain.goalAchievement.dto.GoalAchievementSaveDto;
import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.running.RunningRecord;
import com.dnd.runus.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GoalAchievementService {
    private final GoalAchievementRepository goalAchievementRepository;

    @Transactional
    public GoalAchievementDto save(
            Member member, RunningRecord runningRecord, GoalAchievementSaveDto goalAchievementSaveDto) {
        GoalAchievement savedGoalAchievement = goalAchievementRepository.save(new GoalAchievement(
                member, runningRecord, goalAchievementSaveDto.goalType(), goalAchievementSaveDto.goalValue()));

        return GoalAchievementDto.from(savedGoalAchievement);
    }

    @Transactional(readOnly = true)
    public GoalAchievementDto findByRunningId(RunningRecord runningRecord) {
        GoalAchievement goalAchievement = goalAchievementRepository
                .findByRunningRecordId(runningRecord.runningId())
                .orElseThrow(() -> new NotFoundException(GoalAchievement.class.getSimpleName()
                        + ": can not found GoalAchievement by RunningId(" + runningRecord.runningId()
                        + ")"));

        return GoalAchievementDto.from(goalAchievement);
    }
}
