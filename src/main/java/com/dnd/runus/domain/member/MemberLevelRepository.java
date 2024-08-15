package com.dnd.runus.domain.member;

import java.util.Optional;

public interface MemberLevelRepository {

    MemberLevel save(MemberLevel memberLevel);

    Optional<MemberLevel> findByMemberId(long memberId);

    MemberLevel.Current findByMemberIdWithLevel(long memberId);

    void deleteByMemberId(long memberId);

    void updateMemberLevel(long memberId, int plusExp);
}
