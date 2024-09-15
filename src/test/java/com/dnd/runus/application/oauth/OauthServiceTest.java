package com.dnd.runus.application.oauth;

import com.dnd.runus.auth.oidc.provider.OidcProvider;
import com.dnd.runus.auth.oidc.provider.OidcProviderRegistry;
import com.dnd.runus.auth.token.TokenProviderModule;
import com.dnd.runus.auth.token.dto.AuthTokenDto;
import com.dnd.runus.domain.badge.BadgeAchievementRepository;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievementPercentageRepository;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievementRepository;
import com.dnd.runus.domain.common.Coordinate;
import com.dnd.runus.domain.common.Pace;
import com.dnd.runus.domain.goalAchievement.GoalAchievementRepository;
import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.member.MemberLevelRepository;
import com.dnd.runus.domain.member.MemberRepository;
import com.dnd.runus.domain.member.SocialProfile;
import com.dnd.runus.domain.member.SocialProfileRepository;
import com.dnd.runus.domain.running.RunningRecord;
import com.dnd.runus.domain.running.RunningRecordRepository;
import com.dnd.runus.domain.scale.ScaleAchievementRepository;
import com.dnd.runus.global.constant.MemberRole;
import com.dnd.runus.global.constant.RunningEmoji;
import com.dnd.runus.global.constant.SocialType;
import com.dnd.runus.global.exception.BusinessException;
import com.dnd.runus.global.exception.NotFoundException;
import com.dnd.runus.global.exception.type.ErrorType;
import com.dnd.runus.presentation.v1.oauth.dto.request.SignInRequest;
import com.dnd.runus.presentation.v1.oauth.dto.request.SignUpRequest;
import com.dnd.runus.presentation.v1.oauth.dto.request.WithdrawRequest;
import com.dnd.runus.presentation.v1.oauth.dto.response.SignResponse;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class OauthServiceTest {
    @InjectMocks
    private OauthService oauthService;

    @Mock
    private OidcProviderRegistry oidcProviderRegistry;

    @Mock
    private OidcProvider oidcProvider;

    @Mock
    private SocialProfileRepository socialProfileRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private TokenProviderModule tokenProviderModule;

    @Mock
    private MemberLevelRepository memberLevelRepository;

    @Mock
    private BadgeAchievementRepository badgeAchievementRepository;

    @Mock
    private RunningRecordRepository runningRecordRepository;

    @Mock
    private ChallengeAchievementRepository challengeAchievementRepository;

    @Mock
    private GoalAchievementRepository goalAchievementRepository;

    @Mock
    private ChallengeAchievementPercentageRepository challengeAchievementPercentageRepository;

    @Mock
    private ScaleAchievementRepository scaleAchievementRepository;

    private Member member;

    @BeforeEach
    void setUp() {
        member = new Member(1L, MemberRole.USER, "nickname", OffsetDateTime.now(), OffsetDateTime.now());
    }

    @Nested
    @ExtendWith(MockitoExtension.class)
    @DisplayName("oauth관련 테스트: 회원가입, 로그인, 탈퇴 관련")
    class OauthTest {
        private SocialType socialType;
        private String idToken;
        private String authorizationCode;
        private String oauthId;
        private String email;

        @Mock
        private Claims claims;

        @BeforeEach
        void setUp() {
            socialType = SocialType.APPLE;
            idToken = "idToken";
            authorizationCode = "authorizationCode";
            oauthId = "oauthId";
            email = "oauthEmail@email.com";
        }

        @Test
        @DisplayName("sign-in 시 socialProfile이 있다면 성공")
        void socialProfile_exist_then_signIn_success() {
            // given
            SignInRequest request = new SignInRequest(socialType, idToken);
            given(oidcProviderRegistry.getOidcProviderBy(socialType)).willReturn(oidcProvider);
            given(oidcProvider.getClaimsBy(idToken)).willReturn(claims);
            given(claims.getSubject()).willReturn(oauthId);
            given(claims.get("email")).willReturn(email);
            given(socialProfileRepository.findBySocialTypeAndOauthId(socialType, oauthId))
                    .willReturn(Optional.of(new SocialProfile(1L, member, socialType, oauthId, email)));
            AuthTokenDto tokenDto = new AuthTokenDto("access-token", "refresh-token", "bearer");
            given(tokenProviderModule.generate(String.valueOf(member.memberId())))
                    .willReturn(tokenDto);

            // when
            SignResponse signResponse = oauthService.signIn(request);

            // then
            assertNotNull(signResponse);
            assertEquals(member.nickname(), signResponse.nickname());
            assertEquals(email, signResponse.email());
            assertEquals(tokenDto.accessToken(), signResponse.accessToken());
        }

        @Test
        @DisplayName("sign-in 시 socialProfile이 없다면 에러 타입이 USER_NOT_FOUND인 BusinessException 에러 발생")
        void socialProfile_not_exist_then_signIn_fail() {
            // given
            SignInRequest request = new SignInRequest(socialType, idToken);
            given(oidcProviderRegistry.getOidcProviderBy(socialType)).willReturn(oidcProvider);
            given(oidcProvider.getClaimsBy(idToken)).willReturn(claims);
            given(claims.getSubject()).willReturn(oauthId);
            given(claims.get("email")).willReturn(email);
            given(socialProfileRepository.findBySocialTypeAndOauthId(socialType, oauthId))
                    .willReturn(Optional.empty());

            // when, then
            BusinessException exception = assertThrows(BusinessException.class, () -> oauthService.signIn(request));
            assertEquals(ErrorType.USER_NOT_FOUND, exception.getType());
        }

        @Test
        @DisplayName("sign-up 시 socialProfile이 있다면 해당 socialProfile을 사용")
        void socialProfile_not_exist_then_signUp_success() {
            // given
            SignUpRequest request = new SignUpRequest(socialType, idToken, "nickname");
            given(oidcProviderRegistry.getOidcProviderBy(socialType)).willReturn(oidcProvider);
            given(oidcProvider.getClaimsBy(idToken)).willReturn(claims);
            given(claims.getSubject()).willReturn(oauthId);
            given(claims.get("email")).willReturn(email);
            SocialProfile socialProfile = new SocialProfile(1L, member, socialType, oauthId, email);
            given(socialProfileRepository.findBySocialTypeAndOauthId(socialType, oauthId))
                    .willReturn(Optional.of(socialProfile));
            AuthTokenDto tokenDto = new AuthTokenDto("access-token", "refresh-token", "bearer");
            given(tokenProviderModule.generate(String.valueOf(member.memberId())))
                    .willReturn(tokenDto);

            // when
            SignResponse signResponse = oauthService.signUp(request);

            // then
            assertNotNull(signResponse);
            assertEquals(member.nickname(), signResponse.nickname());
            assertEquals(email, signResponse.email());
            assertEquals(tokenDto.accessToken(), signResponse.accessToken());
        }

        @Test
        @DisplayName("sign-up 시 socialProfile이 없다면 socialProfileRepository.save() 호출")
        void socialProfile_not_exist_then_signUp_save_social_profile() {
            // given
            SignUpRequest request = new SignUpRequest(socialType, idToken, "nickname");
            given(oidcProviderRegistry.getOidcProviderBy(socialType)).willReturn(oidcProvider);
            given(oidcProvider.getClaimsBy(idToken)).willReturn(claims);
            given(claims.getSubject()).willReturn(oauthId);
            given(claims.get("email")).willReturn(email);
            given(socialProfileRepository.findBySocialTypeAndOauthId(socialType, oauthId))
                    .willReturn(Optional.empty());

            Member newMember =
                    new Member(2L, MemberRole.USER, request.nickname(), OffsetDateTime.now(), OffsetDateTime.now());
            SocialProfile socialProfile = new SocialProfile(1L, newMember, socialType, oauthId, email);
            given(socialProfileRepository.save(any(SocialProfile.class))).willReturn(socialProfile);

            AuthTokenDto tokenDto = new AuthTokenDto("access-token", "refresh-token", "bearer");
            given(tokenProviderModule.generate(String.valueOf(newMember.memberId())))
                    .willReturn(tokenDto);

            // when
            SignResponse signResponse = oauthService.signUp(request);

            // then
            assertNotNull(signResponse);
            assertEquals(newMember.nickname(), signResponse.nickname());
            assertEquals(email, signResponse.email());
            assertEquals(tokenDto.accessToken(), signResponse.accessToken());

            then(socialProfileRepository).should().save(any(SocialProfile.class));
        }

        @DisplayName("소셜 로그인 연동 해제: 성공")
        @Test
        void revokeOauth_Success() {

            // given
            WithdrawRequest request = new WithdrawRequest(socialType, authorizationCode, idToken);
            SocialProfile socialProfileMock = new SocialProfile(1L, member, request.socialType(), oauthId, email);

            given(memberRepository.findById(member.memberId())).willReturn(Optional.of(member));
            given(oidcProviderRegistry.getOidcProviderBy(request.socialType())).willReturn(oidcProvider);
            given(oidcProvider.getClaimsBy(request.idToken())).willReturn(claims);
            given(claims.getSubject()).willReturn(oauthId);
            given(socialProfileRepository.findBySocialTypeAndOauthId(request.socialType(), oauthId))
                    .willReturn(Optional.of(socialProfileMock));

            String accessToken = "accessToken";
            given(oidcProvider.getAccessToken(request.authorizationCode())).willReturn(accessToken);

            // when
            oauthService.revokeOauth(member.memberId(), request);

            // then
            then(oidcProvider).should().revoke(accessToken);
        }

        @DisplayName("소셜 로그인 연동 해제: member_id가 없을 경우 NotFoundException을 발생한다.")
        @Test
        void revokeOauth_NotFound_MemberID() {
            // given
            WithdrawRequest request = new WithdrawRequest(socialType, authorizationCode, idToken);
            given(memberRepository.findById(member.memberId())).willReturn(Optional.empty());

            // when, then
            assertThrows(NotFoundException.class, () -> oauthService.revokeOauth(member.memberId(), request));
        }

        @DisplayName("소셜 로그인 연동 해제: oauthId와 socialType에 해당하는 socialProfile 없을 경우 NotFoundException을 발생한다.")
        @Test
        void revokeOauth_NotFound_SocialProfile() {
            // given
            WithdrawRequest request = new WithdrawRequest(socialType, authorizationCode, idToken);
            given(memberRepository.findById(member.memberId())).willReturn(Optional.of(member));
            given(oidcProviderRegistry.getOidcProviderBy(request.socialType())).willReturn(oidcProvider);
            given(oidcProvider.getClaimsBy(request.idToken())).willReturn(claims);
            given(claims.getSubject()).willReturn(oauthId);
            given(socialProfileRepository.findBySocialTypeAndOauthId(request.socialType(), oauthId))
                    .willReturn(Optional.empty());

            // when, then
            assertThrows(NotFoundException.class, () -> oauthService.revokeOauth(member.memberId(), request));
        }

        @DisplayName("소셜 로그인 연동 해제: socialProfile의 memberId와 member의 id가 다를 경우 NotFoundException을 발생한다.")
        @Test
        void revokeOauth_MissMatch_socialProfileAndMemberId() {
            // given
            WithdrawRequest request = new WithdrawRequest(socialType, authorizationCode, idToken);
            SocialProfile socialProfileMock = new SocialProfile(
                    1L,
                    new Member(2L, MemberRole.USER, "nickname", OffsetDateTime.now(), OffsetDateTime.now()),
                    request.socialType(),
                    oauthId,
                    email);

            given(memberRepository.findById(member.memberId())).willReturn(Optional.of(member));
            given(oidcProviderRegistry.getOidcProviderBy(request.socialType())).willReturn(oidcProvider);
            given(oidcProvider.getClaimsBy(request.idToken())).willReturn(claims);
            given(claims.getSubject()).willReturn(oauthId);
            given(socialProfileRepository.findBySocialTypeAndOauthId(request.socialType(), oauthId))
                    .willReturn(Optional.of(socialProfileMock));

            // when, then
            assertThrows(NotFoundException.class, () -> oauthService.revokeOauth(member.memberId(), request));
        }
    }

    @Nested
    @ExtendWith(MockitoExtension.class)
    @DisplayName("회원 탈퇴를 위한 멤버 데이터 삭제")
    class DeleteAllMemberTest {

        @DisplayName("회원 삭제: 회원이 존재하지 않으면 NotFoundException을 발생하한다.")
        @Test
        public void testDeleteAllDataAboutMember_MemberNotFound() {
            given(memberRepository.findById(member.memberId())).willReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> oauthService.deleteAllDataAboutMember(member.memberId()));
        }

        @DisplayName("회원 삭제 : running_record 존재 X")
        @Test
        void testDeleteAllDataAboutMember_NoRunningRecords() {
            // given
            given(memberRepository.findById(member.memberId())).willReturn(Optional.of(member));
            given(runningRecordRepository.findByMember(member)).willReturn(Collections.emptyList());

            // when
            oauthService.deleteAllDataAboutMember(member.memberId());

            // then
            then(memberLevelRepository).should().deleteByMemberId(member.memberId());
            then(badgeAchievementRepository).should().deleteByMemberId(member.memberId());
            then(scaleAchievementRepository).should().deleteByMemberId(member.memberId());
            then(socialProfileRepository).should().deleteByMemberId(member.memberId());
            then(memberRepository).should().deleteById(member.memberId());
        }

        @DisplayName("회원 삭제 : running_record 존재, challenge_achievement 존재 X")
        @Test
        void testDeleteAllDataAboutMember_WithRunningRecords_NoChallengeAchievement() {
            // given
            List<RunningRecord> runningRecords = List.of(new RunningRecord(
                    1L,
                    member,
                    1_100_000,
                    Duration.ofHours(12).plusMinutes(23).plusSeconds(56),
                    1,
                    new Pace(5, 11),
                    OffsetDateTime.now(),
                    OffsetDateTime.now().plusHours(1),
                    List.of(new Coordinate(1, 2, 3), new Coordinate(4, 5, 6)),
                    "start location",
                    "end location",
                    RunningEmoji.SOSO));

            given(memberRepository.findById(member.memberId())).willReturn(Optional.of(member));
            given(runningRecordRepository.findByMember(member)).willReturn(runningRecords);
            given(challengeAchievementRepository.findIdsByRunningRecords(runningRecords))
                    .willReturn(Collections.emptyList());

            // when
            oauthService.deleteAllDataAboutMember(member.memberId());

            // then
            then(memberLevelRepository).should().deleteByMemberId(member.memberId());
            then(badgeAchievementRepository).should().deleteByMemberId(member.memberId());
            then(scaleAchievementRepository).should().deleteByMemberId(member.memberId());
            then(socialProfileRepository).should().deleteByMemberId(member.memberId());
            then(goalAchievementRepository).should().deleteByRunningRecords(runningRecords);
            then(runningRecordRepository).should().deleteByMemberId(member.memberId());
            then(memberRepository).should().deleteById(member.memberId());
        }

        @DisplayName("회원 삭제 : running_record, challenge_achievement 존재")
        @Test
        void testDeleteAllDataAboutMember_WithRunningRecords_WithChallengeAchievement() {
            // given
            List<RunningRecord> runningRecords = List.of(new RunningRecord(
                    1L,
                    member,
                    1_100_000,
                    Duration.ofHours(12).plusMinutes(23).plusSeconds(56),
                    1,
                    new Pace(5, 11),
                    OffsetDateTime.now(),
                    OffsetDateTime.now().plusHours(1),
                    List.of(new Coordinate(1, 2, 3), new Coordinate(4, 5, 6)),
                    "start location",
                    "end location",
                    RunningEmoji.SOSO));

            List<Long> challengeAchievementIds = List.of(1L);

            given(memberRepository.findById(member.memberId())).willReturn(Optional.of(member));
            given(runningRecordRepository.findByMember(member)).willReturn(runningRecords);
            given(challengeAchievementRepository.findIdsByRunningRecords(runningRecords))
                    .willReturn(challengeAchievementIds);

            // when
            oauthService.deleteAllDataAboutMember(member.memberId());

            // then
            then(memberLevelRepository).should().deleteByMemberId(member.memberId());
            then(badgeAchievementRepository).should().deleteByMemberId(member.memberId());
            then(scaleAchievementRepository).should().deleteByMemberId(member.memberId());
            then(socialProfileRepository).should().deleteByMemberId(member.memberId());
            then(goalAchievementRepository).should().deleteByRunningRecords(runningRecords);
            then(challengeAchievementPercentageRepository)
                    .should()
                    .deleteByChallengeAchievementIds(challengeAchievementIds);
            then(challengeAchievementRepository).should().deleteByIds(challengeAchievementIds);
            then(runningRecordRepository).should().deleteByMemberId(member.memberId());
            then(memberRepository).should().deleteById(member.memberId());
        }
    }
}
