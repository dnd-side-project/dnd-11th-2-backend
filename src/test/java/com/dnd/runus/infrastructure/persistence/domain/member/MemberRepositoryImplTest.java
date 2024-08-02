package com.dnd.runus.infrastructure.persistence.domain.member;

import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.member.MemberRepository;
import com.dnd.runus.global.constant.MemberRole;
import com.dnd.runus.infrastructure.persistence.annotation.RepositoryTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RepositoryTest
class MemberRepositoryImplTest {
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("findById 메서드는 Member를 반환한다.")
    void findById() {
        Member member = memberRepository.findById(1L).orElse(null);
        System.out.println(member);
    }

    @Test
    @DisplayName("save 메서드는 Member를 저장하고 id와 함께 반환한다.")
    void save() {
        Member member = Member.builder()
                .role(MemberRole.USER)
                .nickname("nickname")
                .weightKg(60)
                .mainBadge(null)
                .createdAt(null)
                .updatedAt(null)
                .level(null)
                .currentExp(0)
                .build();
        Member savedMember = memberRepository.save(member);
        assertNotEquals(0, savedMember.memberId());
        assertNotNull(savedMember.createdAt());
        assertNotNull(savedMember.updatedAt());
    }
}
