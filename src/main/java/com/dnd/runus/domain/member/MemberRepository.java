package com.dnd.runus.domain.member;

import java.util.Optional;

public interface MemberRepository {
    Optional<Member> findById(long id);

    Member save(Member member);

    void deleteById(long memberId);
}
