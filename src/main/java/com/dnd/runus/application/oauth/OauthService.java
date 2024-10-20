package com.dnd.runus.application.oauth;

import com.dnd.runus.application.member.MemberWithdrawService;
import com.dnd.runus.auth.exception.AuthException;
import com.dnd.runus.auth.oidc.provider.OidcProvider;
import com.dnd.runus.auth.oidc.provider.OidcProviderRegistry;
import com.dnd.runus.auth.token.TokenProviderModule;
import com.dnd.runus.auth.token.dto.AuthTokenDto;
import com.dnd.runus.domain.member.*;
import com.dnd.runus.global.constant.MemberRole;
import com.dnd.runus.global.constant.SocialType;
import com.dnd.runus.global.event.AfterTransactionEvent;
import com.dnd.runus.global.exception.BusinessException;
import com.dnd.runus.global.exception.NotFoundException;
import com.dnd.runus.global.exception.type.ErrorType;
import com.dnd.runus.presentation.v1.oauth.dto.request.SignInRequest;
import com.dnd.runus.presentation.v1.oauth.dto.request.SignUpRequest;
import com.dnd.runus.presentation.v1.oauth.dto.request.WithdrawRequest;
import com.dnd.runus.presentation.v1.oauth.dto.response.SignResponse;
import io.jsonwebtoken.Claims;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OauthService {

    private final OidcProviderRegistry oidcProviderRegistry;
    private final TokenProviderModule tokenProviderModule;

    private final MemberRepository memberRepository;
    private final SocialProfileRepository socialProfileRepository;
    private final MemberLevelRepository memberLevelRepository;

    private final MemberWithdrawService memberWithdrawService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public SignResponse signIn(SignInRequest request) {
        OidcProvider oidcProvider = oidcProviderRegistry.getOidcProviderBy(request.socialType());
        Claims claim = oidcProvider.getClaimsBy(request.idToken());
        String oauthId = claim.getSubject();
        String email = extractAndValidateEmail(claim, request.socialType());

        SocialProfile socialProfile = socialProfileRepository
                .findBySocialTypeAndOauthId(request.socialType(), oauthId)
                .orElseThrow(
                        () -> new BusinessException(ErrorType.USER_NOT_FOUND, "socialType: " + request.socialType()));

        updateEmailIfChanged(socialProfile, email);
        AuthTokenDto tokenDto = tokenProviderModule.generate(
                String.valueOf(socialProfile.member().memberId()));
        return SignResponse.from(socialProfile.member().nickname(), socialProfile.oauthEmail(), tokenDto);
    }

    @Transactional
    public SignResponse signUp(SignUpRequest request) {
        OidcProvider oidcProvider = oidcProviderRegistry.getOidcProviderBy(request.socialType());
        Claims claim = oidcProvider.getClaimsBy(request.idToken());
        String oauthId = claim.getSubject();
        String email = extractAndValidateEmail(claim, request.socialType());

        // 기존 사용자 없을 경우 insert
        SocialProfile socialProfile = socialProfileRepository
                .findBySocialTypeAndOauthId(request.socialType(), oauthId)
                .orElseGet(() -> createMember(oauthId, email, request.socialType(), request.nickname()));

        updateEmailIfChanged(socialProfile, email);
        AuthTokenDto tokenDto = tokenProviderModule.generate(
                String.valueOf(socialProfile.member().memberId()));
        return SignResponse.from(socialProfile.member().nickname(), socialProfile.oauthEmail(), tokenDto);
    }

    @Transactional(readOnly = true)
    public boolean revokeOauth(long memberId, WithdrawRequest request) {

        memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(Member.class, memberId));

        OidcProvider oidcProvider = oidcProviderRegistry.getOidcProviderBy(request.socialType());

        String oauthId = oidcProvider.getClaimsBy(request.idToken()).getSubject();

        socialProfileRepository
                .findBySocialTypeAndOauthId(request.socialType(), oauthId)
                .filter(profile -> profile.member().memberId() == memberId)
                .orElseThrow(() -> new AuthException(
                        ErrorType.INVALID_CREDENTIALS,
                        String.format(
                                "socialType: %s, oauthId: %s, memberId: %s", request.socialType(), oauthId, memberId)));

        try {
            String accessToken = oidcProvider.getAccessToken(request.authorizationCode());
            oidcProvider.revoke(accessToken);
            log.info("토큰 revoke 성공. memberId: {}, socialType: {}", memberId, request.socialType());

            AfterTransactionEvent withDrawEvent = () -> memberWithdrawService.deleteAllDataAboutMember(memberId);
            eventPublisher.publishEvent(withDrawEvent);

            return true;
        } catch (Exception e) {
            log.warn("토큰 revoke 실패. memberId: {}, socialType: {}, {}", memberId, request.socialType(), e.getMessage());
            return false;
        }
    }

    private SocialProfile createMember(String oauthId, String email, SocialType socialType, String nickname) {
        Member member = memberRepository.save(new Member(MemberRole.USER, nickname));

        // default level 설정
        memberLevelRepository.save(new MemberLevel(member));

        return socialProfileRepository.save(SocialProfile.builder()
                .member(member)
                .socialType(socialType)
                .oauthId(oauthId)
                .oauthEmail(email)
                .build());
    }

    private String extractAndValidateEmail(Claims claim, SocialType socialType) {
        String email = (String) claim.get("email");
        if (StringUtils.isBlank(email)) {
            log.warn("Failed to get email from idToken! type: {}, claim: {}", socialType, claim);
            throw new AuthException(ErrorType.FAILED_AUTHENTICATION, "Failed to get email from idToken");
        }
        return email;
    }

    private void updateEmailIfChanged(SocialProfile socialProfile, String email) {
        if (!email.equals(socialProfile.oauthEmail())) {
            socialProfileRepository.updateOauthEmail(socialProfile.socialProfileId(), email);
        }
    }
}
