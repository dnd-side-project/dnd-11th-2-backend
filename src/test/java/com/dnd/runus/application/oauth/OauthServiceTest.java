package com.dnd.runus.application.oauth;

import com.dnd.runus.auth.exception.AuthException;
import com.dnd.runus.auth.oidc.provider.OidcProvider;
import com.dnd.runus.auth.oidc.provider.OidcProviderRegistry;
import com.dnd.runus.auth.token.TokenProviderModule;
import com.dnd.runus.auth.token.dto.AuthTokenDto;
import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.member.SocialProfile;
import com.dnd.runus.global.constant.MemberRole;
import com.dnd.runus.global.constant.SocialType;
import com.dnd.runus.global.exception.BusinessException;
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
import org.springframework.context.ApplicationEventPublisher;

import java.time.OffsetDateTime;
import java.util.concurrent.CountDownLatch;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
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
    private SocialProfileService socialProfileService;

    @Mock
    private TokenProviderModule tokenProviderModule;

    @Mock
    private ApplicationEventPublisher eventPublisher;

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
            given(socialProfileService.findOrThrow(socialType, oauthId, email))
                    .willReturn(new SocialProfile(1L, member, socialType, oauthId, email));
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
        @DisplayName("sign-in 시 socialProfile이 없다면 에러 타입이 SOCIAL_MEMBER_NOT_FOUND인 BusinessException 에러 발생")
        void socialProfile_not_exist_then_signIn_fail() {
            // given
            SignInRequest request = new SignInRequest(socialType, idToken);
            given(oidcProviderRegistry.getOidcProviderBy(socialType)).willReturn(oidcProvider);
            given(oidcProvider.getClaimsBy(idToken)).willReturn(claims);
            given(claims.getSubject()).willReturn(oauthId);
            given(claims.get("email")).willReturn(email);
            given(socialProfileService.findOrThrow(socialType, oauthId, email))
                    .willThrow(new BusinessException(ErrorType.SOCIAL_MEMBER_NOT_FOUND));

            // when, then
            BusinessException exception = assertThrows(BusinessException.class, () -> oauthService.signIn(request));
            assertEquals(ErrorType.SOCIAL_MEMBER_NOT_FOUND, exception.getType());
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
            given(socialProfileService.findOrCreate(socialType, oauthId, email, request.nickname()))
                    .willReturn(socialProfile);
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
        @DisplayName("sign-up 시 socialProfile이 없다면 socialProfile을 생성")
        void socialProfile_not_exist_then_signUp_save_social_profile() {
            // given
            SignUpRequest request = new SignUpRequest(socialType, idToken, "nickname");
            given(oidcProviderRegistry.getOidcProviderBy(socialType)).willReturn(oidcProvider);
            given(oidcProvider.getClaimsBy(idToken)).willReturn(claims);
            given(claims.getSubject()).willReturn(oauthId);
            given(claims.get("email")).willReturn(email);

            Member newMember =
                    new Member(2L, MemberRole.USER, request.nickname(), OffsetDateTime.now(), OffsetDateTime.now());
            SocialProfile socialProfile = new SocialProfile(1L, newMember, socialType, oauthId, email);

            given(socialProfileService.findOrCreate(socialType, oauthId, email, request.nickname()))
                    .willReturn(socialProfile);

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

            then(socialProfileService).should().findOrCreate(socialType, oauthId, email, request.nickname());
        }

        @DisplayName("소셜 로그인 연동 해제: 성공")
        @Test
        void revokeOauth_Success() throws InterruptedException {

            // given
            WithdrawRequest request = new WithdrawRequest(socialType, authorizationCode, idToken);

            given(oidcProviderRegistry.getOidcProviderBy(request.socialType())).willReturn(oidcProvider);
            given(oidcProvider.getClaimsBy(request.idToken())).willReturn(claims);
            given(claims.getSubject()).willReturn(oauthId);
            given(socialProfileService.isSocialMemberExists(request.socialType(), oauthId, member.memberId()))
                    .willReturn(true);

            CountDownLatch latch = new CountDownLatch(2);

            String accessToken = "accessToken";
            given(oidcProvider.getAccessToken(request.authorizationCode())).willAnswer(invocation -> {
                latch.countDown();
                return accessToken;
            });

            will(invocation -> {
                        latch.countDown();
                        return null;
                    })
                    .given(oidcProvider)
                    .revoke(accessToken);

            // when
            oauthService.revokeOauth(member.memberId(), request);

            // then
            boolean completed = latch.await(100, MILLISECONDS);
            assertTrue(completed);
        }

        @DisplayName("소셜 로그인 연동 해제: oauthId와 socialType에 해당하는 socialProfile 없을 경우 AuthException을 발생한다.")
        @Test
        void revokeOauth_NotFound_SocialProfile() {
            // given
            WithdrawRequest request = new WithdrawRequest(socialType, authorizationCode, idToken);

            given(oidcProviderRegistry.getOidcProviderBy(request.socialType())).willReturn(oidcProvider);
            given(oidcProvider.getClaimsBy(request.idToken())).willReturn(claims);
            given(claims.getSubject()).willReturn(oauthId);
            given(socialProfileService.isSocialMemberExists(request.socialType(), oauthId, member.memberId()))
                    .willReturn(false);

            // when, then
            assertThrows(AuthException.class, () -> oauthService.revokeOauth(member.memberId(), request));
        }

        @DisplayName("소셜 로그인 연동 해제: socialProfile의 memberId와 member의 id가 다를 경우 AuthException을 발생한다.")
        @Test
        void revokeOauth_MissMatch_socialProfileAndMemberId() {
            // given
            WithdrawRequest request = new WithdrawRequest(socialType, authorizationCode, idToken);

            given(oidcProviderRegistry.getOidcProviderBy(request.socialType())).willReturn(oidcProvider);
            given(oidcProvider.getClaimsBy(request.idToken())).willReturn(claims);
            given(claims.getSubject()).willReturn(oauthId);
            given(socialProfileService.isSocialMemberExists(request.socialType(), oauthId, member.memberId()))
                    .willReturn(false);

            // when, then
            assertThrows(AuthException.class, () -> oauthService.revokeOauth(member.memberId(), request));
        }
    }
}
