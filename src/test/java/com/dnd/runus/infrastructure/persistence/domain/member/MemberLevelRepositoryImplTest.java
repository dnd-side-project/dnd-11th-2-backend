package com.dnd.runus.infrastructure.persistence.domain.member;

import com.dnd.runus.domain.level.Level;
import com.dnd.runus.domain.level.LevelRepository;
import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.member.MemberLevel;
import com.dnd.runus.domain.member.MemberLevelRepository;
import com.dnd.runus.domain.member.MemberRepository;
import com.dnd.runus.global.constant.MemberRole;
import com.dnd.runus.infrastructure.persistence.annotation.RepositoryTest;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@RepositoryTest
class MemberLevelRepositoryImplTest {

    @Autowired
    private MemberLevelRepository memberLevelRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member savedMember;

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
        memberLevelRepository.save(new MemberLevel(0, savedMember, 1, 100));

        // when
        memberLevelRepository.deleteByMemberId(savedMember.memberId());

        // then
        assertFalse(memberLevelRepository.findByMemberId(savedMember.memberId()).isPresent());
    }

    @Nested
    @DisplayName("멤버 레벨 수정")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class MemberLevelUpdateTest {
        @Autowired
        private LevelRepository levelRepository;

        @BeforeAll
        void beforeAll() {
            levelRepository.save(new Level(1, 0, 100));
            levelRepository.save(new Level(2, 101, 200));
            levelRepository.save(new Level(3, 201, Integer.MAX_VALUE));
        }

        @BeforeEach
        void setUp() {
            memberLevelRepository.save(new MemberLevel(0, savedMember, 1, 50));
        }

        @AfterEach
        void tearDown() {
            memberLevelRepository.deleteByMemberId(savedMember.memberId());
        }

        @ParameterizedTest
        @MethodSource("provideMemberLevelArgs")
        @DisplayName("경험치가 주어지면, 경험치와 member level을 경험치와 맞는 level로 수정한다.")
        void updateMemberLevel(int plusExp, int expectedExp, long expectedLevelId) {
            // when
            MemberLevel.Summary updatedMemberLevel =
                    memberLevelRepository.updateMemberLevel(savedMember.memberId(), plusExp);

            // then
            assertEquals(expectedExp, updatedMemberLevel.exp());
            assertEquals(expectedLevelId, updatedMemberLevel.levelId());
        }

        private static Stream<Arguments> provideMemberLevelArgs() {
            return Stream.of(Arguments.of(10, 60, 1), Arguments.of(100, 150, 2));
        }
    }
}
