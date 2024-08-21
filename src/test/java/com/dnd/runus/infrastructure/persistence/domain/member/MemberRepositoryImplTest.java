package com.dnd.runus.infrastructure.persistence.domain.member;

import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.member.MemberRepository;
import com.dnd.runus.global.constant.MemberRole;
import com.dnd.runus.infrastructure.persistence.annotation.RepositoryTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

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

    @Test
    @DisplayName("Member를 삭제한다.")
    void delete() {
        // given
        Member member = new Member(MemberRole.USER, "nickname");
        long savedMemberId = memberRepository.save(member).memberId();

        // when
        memberRepository.deleteById(savedMemberId);

        // then
        assertFalse(memberRepository.findById(savedMemberId).isPresent());
    }

    @Transactional
    @DisplayName("사용자 nickname을 update한다.")
    @Test
    void updateNickName() {
        // given
        Member member = memberRepository.save(new Member(MemberRole.USER, "nickname"));
        String newNickName = "newNickName";

        // when
        memberRepository.updateNicknameById(member.memberId(), newNickName);

        // then
        Member updatedMember = memberRepository.findById(member.memberId()).orElse(null);
        assertNotNull(updatedMember);
        assertThat(updatedMember.nickname()).isEqualTo(newNickName);
    }
}
