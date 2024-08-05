package com.dnd.runus.infrastructure.persistence.domain.member;

import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.member.MemberRepository;
import com.dnd.runus.global.constant.MemberRole;
import com.dnd.runus.infrastructure.persistence.annotation.RepositoryTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

@RepositoryTest
class MemberRepositoryImplTest {
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("findById 메서드는 해당하는 id의 Member가 없다면 null을 반환한다.")
    void findById() {
        Member member = memberRepository.findById(1L).orElse(null);
        assertNull(member);
    }

    @Test
    @DisplayName("save 메서드는 Member를 저장하고 id와 함께 반환한다.")
    void save() {
        Member member = new Member(MemberRole.USER, "nickname");
        Member savedMember = memberRepository.save(member);
        assertNotEquals(0, savedMember.memberId());
        assertNotNull(savedMember.createdAt());
        assertNotNull(savedMember.updatedAt());
    }
}
