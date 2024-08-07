package com.dnd.runus.domain.running;

import com.dnd.runus.domain.member.Member;

public interface RunningRecordRepository {

    void deleteByMember(Member member);
}
