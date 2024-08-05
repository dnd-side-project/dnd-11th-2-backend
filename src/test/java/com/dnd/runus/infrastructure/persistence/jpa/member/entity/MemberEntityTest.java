package com.dnd.runus.infrastructure.persistence.jpa.member.entity;

import com.dnd.runus.domain.badge.Badge;
import com.dnd.runus.domain.member.Member;
import com.dnd.runus.global.constant.BadgeType;
import com.dnd.runus.global.constant.MemberRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MemberEntityTest {
    private Member member;

    @BeforeEach
    void setUp() {
        Badge badge =
                new Badge(1L, "badge name", "badge description", "https://badge.com", BadgeType.DISTANCE_METER, 1000);
        member = Member.builder()
                .memberId(1L)
                .role(MemberRole.USER)
                .nickname("nickname")
                .weightKg(60)
                .mainBadge(badge)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
    }

    @Test
    @DisplayName("올바른 Member가 주어질 때, MemberEntity.from() 메서드는 성공한다.")
    void from() {
        MemberEntity memberEntity = MemberEntity.from(member);
        assertEquals(member.memberId(), memberEntity.getId());
    }

    @Test
    @DisplayName("올바른 MemberEntity가 주어질 때, MemberEntity.toDomain() 메서드는 성공한다.")
    void toDomain() {
        MemberEntity memberEntity = MemberEntity.from(member);
        Member member = memberEntity.toDomain();
        assertEquals(memberEntity.getId(), member.memberId());
    }
}
