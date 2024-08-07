package com.dnd.runus.infrastructure.persistence.domain.member;

import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.member.MemberRepository;
import com.dnd.runus.domain.member.SocialProfile;
import com.dnd.runus.domain.member.SocialProfileRepository;
import com.dnd.runus.global.constant.MemberRole;
import com.dnd.runus.global.constant.SocialType;
import com.dnd.runus.infrastructure.persistence.annotation.RepositoryTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RepositoryTest
class SocialProfileRepositoryImplTest {

    @Autowired
    private SocialProfileRepository socialProfileRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member savedMember;

    @BeforeEach
    void beforeEach() {
        // Member의 자식임으로 테스트 시 임의이 Member 추가
        Member member = new Member(MemberRole.USER, "nickname");
        savedMember = memberRepository.save(member);
    }

    @DisplayName("소셜 프로파일 저장한다.")
    @Test
    void save() {
        // given
        String oauthId = "oauthId";
        SocialType socialType = SocialType.APPLE;
        String email = "email@email.com";

        // when
        SocialProfile savedSocialProfile = socialProfileRepository.save(SocialProfile.builder()
                .member(savedMember)
                .socialType(socialType)
                .oauthId(oauthId)
                .oauthEmail(email)
                .build());

        // then
        assertNotEquals(0, savedSocialProfile.socialProfileId());
        assertEquals(oauthId, savedSocialProfile.oauthId());
        assertEquals(socialType, socialType);
        assertEquals(email, email);
    }

    @DisplayName("findById socialProfile 조회한다.")
    @Test
    void findById() {
        // given
        String oauthId = "oauthId";
        SocialType socialType = SocialType.APPLE;
        String email = "email@email.com";

        SocialProfile savedSocialProfile = socialProfileRepository.save(SocialProfile.builder()
                .member(savedMember)
                .socialType(socialType)
                .oauthId(oauthId)
                .oauthEmail(email)
                .build());

        // when & then
        assertTrue(socialProfileRepository
                .findById(savedSocialProfile.socialProfileId())
                .isPresent());
    }

    @DisplayName("SocialType과 OauthId로 socialProfile 조회한다.")
    @Test
    void findBySocialTypeAndOauthId() {
        // given
        String oauthId = "oauthId";
        SocialType socialType = SocialType.APPLE;
        String email = "email@email.com";

        socialProfileRepository.save(SocialProfile.builder()
                .member(savedMember)
                .socialType(socialType)
                .oauthId(oauthId)
                .oauthEmail(email)
                .build());

        // when & then
        assertTrue(socialProfileRepository
                .findBySocialTypeAndOauthId(socialType, oauthId)
                .isPresent());
    }

    @DisplayName("email를 update한다.")
    @Test
    void updateOauthEmail() {
        // given
        String oauthId = "oauthId";
        SocialType socialType = SocialType.APPLE;
        String preEmail = "preEmail@email.com";

        String updateEmail = "updateEmail@email.com";

        SocialProfile savedSocialProfile = socialProfileRepository.save(SocialProfile.builder()
                .member(savedMember)
                .socialType(socialType)
                .oauthId(oauthId)
                .oauthEmail(preEmail)
                .build());

        // when
        socialProfileRepository.updateOauthEmail(savedSocialProfile.socialProfileId(), updateEmail);

        // then
        SocialProfile updated = socialProfileRepository
                .findById(savedSocialProfile.socialProfileId())
                .orElse(SocialProfile.builder().build());
        assertEquals(updateEmail, updated.oauthEmail());
    }

    @DisplayName("Member로 소설프로파일 데이터를 삭제한다.")
    @Test
    void deleteByMember() {
        // given
        String oauthId = "oauthId";
        SocialType socialType = SocialType.APPLE;
        String email = "email@email.com";

        SocialProfile savedSocialProfile = socialProfileRepository.save(SocialProfile.builder()
                .member(savedMember)
                .socialType(socialType)
                .oauthId(oauthId)
                .oauthEmail(email)
                .build());

        // when
        socialProfileRepository.deleteByMemberId(savedMember.memberId());

        // then
        assertFalse(socialProfileRepository
                .findById(savedSocialProfile.socialProfileId())
                .isPresent());
    }
}
