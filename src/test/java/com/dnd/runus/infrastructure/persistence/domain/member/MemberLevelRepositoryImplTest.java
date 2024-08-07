package com.dnd.runus.infrastructure.persistence.domain.member;

import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.member.MemberLevelRepository;
import com.dnd.runus.domain.member.MemberRepository;
import com.dnd.runus.global.constant.MemberRole;
import com.dnd.runus.infrastructure.persistence.annotation.RepositoryTest;
import com.dnd.runus.infrastructure.persistence.jpa.member.JpaMemberLevelRepository;
import com.dnd.runus.infrastructure.persistence.jpa.member.entity.MemberLevelEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertFalse;

@RepositoryTest
class MemberLevelRepositoryImplTest {

    @Autowired
    private MemberLevelRepository memberLevelRepository;

    @Autowired
    private JpaMemberLevelRepository jpaMemberLevelRepository;

    @Autowired
    private MemberRepository memberRepository;

    private static Member savedMember;

    @BeforeEach
    void beforeEach() {
        // Member의 자식임으로 테스트 시 임의이 Member 추가
        Member member = new Member(MemberRole.USER, "nickname");
        savedMember = memberRepository.save(member);
    }

    @DisplayName("멤버 레벨을 member id로 삭제한다.")
    @Test
    void deleteByMemberId() {
        // given
        MemberLevelEntity savedMemberLevel = jpaMemberLevelRepository.save(MemberLevelEntity.of(savedMember, 1L, 100));

        // when
        memberLevelRepository.deleteByMemberId(savedMember.memberId());

        // then
        assertFalse(jpaMemberLevelRepository.findById(savedMemberLevel.getId()).isPresent());
    }
}
