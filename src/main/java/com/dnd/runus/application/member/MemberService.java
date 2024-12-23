package com.dnd.runus.application.member;

import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.member.MemberLevel;
import com.dnd.runus.domain.member.MemberLevelRepository;
import com.dnd.runus.presentation.v1.member.dto.response.MyProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberLevelRepository memberLevelRepository;

    @Transactional
    public void initMember(Member member) {
        memberLevelRepository.save(new MemberLevel(member));
    }

    @Transactional(readOnly = true)
    public MyProfileResponse getMyProfile(long memberId) {
        MemberLevel.Current memberCurrentLevel = memberLevelRepository.findByMemberIdWithLevel(memberId);

        long currentLevel = memberCurrentLevel.level().levelId();
        long nextLevel = currentLevel + 1;

        return new MyProfileResponse(
                memberCurrentLevel.level().imageUrl(),
                currentLevel,
                memberCurrentLevel.currentExp(),
                nextLevel,
                memberCurrentLevel.level().expRangeStart(),
                memberCurrentLevel.level().expRangeEnd());
    }

    @Transactional
    public void addExp(long memberId, int plusExp) {
        memberLevelRepository.updateMemberLevel(memberId, plusExp);
    }
}
