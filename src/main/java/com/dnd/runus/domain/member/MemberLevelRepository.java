package com.dnd.runus.domain.member;

import java.util.Optional;

public interface MemberLevelRepository {

    MemberLevel save(MemberLevel memberLevel);

    Optional<MemberLevel> findByMemberId(long memberLevelId);

    void deleteByMemberId(long memberId);

    MemberLevel.Summary updateMemberLevel(long memberId, int exp);
}
