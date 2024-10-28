package com.dnd.runus.application.oauth;

import com.dnd.runus.application.member.event.SignupEvent;
import com.dnd.runus.application.member.event.WithdrawEvent;
import com.dnd.runus.auth.exception.AuthException;
import com.dnd.runus.auth.oidc.provider.OidcProvider;
import com.dnd.runus.auth.oidc.provider.OidcProviderRegistry;
import com.dnd.runus.auth.token.TokenProviderModule;
import com.dnd.runus.auth.token.dto.AuthTokenDto;
import com.dnd.runus.domain.member.SocialProfile;
import com.dnd.runus.global.constant.SocialType;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class OauthService {

    private final OidcProviderRegistry oidcProviderRegistry;
    private final TokenProviderModule tokenProviderModule;

    private final SocialProfileService socialProfileService;
    private final ApplicationEventPublisher eventPublisher;

    public SignResponse signIn(SignInRequest request) {
        OidcProvider oidcProvider = oidcProviderRegistry.getOidcProviderBy(request.socialType());
        Claims claim = oidcProvider.getClaimsBy(request.idToken());
        String oauthId = claim.getSubject();
        String email = extractAndValidateEmail(claim, request.socialType());

        SocialProfile socialProfile = socialProfileService.findOrThrow(request.socialType(), oauthId, email);

        AuthTokenDto tokenDto = tokenProviderModule.generate(
                String.valueOf(socialProfile.member().memberId()));
        return SignResponse.from(socialProfile.member().nickname(), socialProfile.oauthEmail(), tokenDto);
    }

    public SignResponse signUp(SignUpRequest request) {
        OidcProvider oidcProvider = oidcProviderRegistry.getOidcProviderBy(request.socialType());
        Claims claim = oidcProvider.getClaimsBy(request.idToken());
        String oauthId = claim.getSubject();
        String email = extractAndValidateEmail(claim, request.socialType());

        SocialProfile socialProfile =
                socialProfileService.findOrCreate(request.socialType(), oauthId, email, request.nickname());

        AuthTokenDto tokenDto = tokenProviderModule.generate(
                String.valueOf(socialProfile.member().memberId()));

        eventPublisher.publishEvent(new SignupEvent(socialProfile.member()));

        return SignResponse.from(socialProfile.member().nickname(), socialProfile.oauthEmail(), tokenDto);
    }

    public boolean revokeOauth(long memberId, WithdrawRequest request) {
        OidcProvider oidcProvider = oidcProviderRegistry.getOidcProviderBy(request.socialType());

        String oauthId = oidcProvider.getClaimsBy(request.idToken()).getSubject();

        if (!socialProfileService.isSocialMemberExists(request.socialType(), oauthId, memberId)) {
            String message =
                    String.format("socialType: %s, oauthId: %s, memberId: %s", request.socialType(), oauthId, memberId);
            throw new AuthException(ErrorType.INVALID_CREDENTIALS, message);
        }

        try {
            String accessToken = oidcProvider.getAccessToken(request.authorizationCode());
            oidcProvider.revoke(accessToken);
            log.info("토큰 revoke 성공. memberId: {}, socialType: {}", memberId, request.socialType());

            eventPublisher.publishEvent(new WithdrawEvent(memberId));

            return true;
        } catch (Exception e) {
            log.warn("토큰 revoke 실패. memberId: {}, socialType: {}, {}", memberId, request.socialType(), e.getMessage());
            return false;
        }
    }

    private String extractAndValidateEmail(Claims claim, SocialType socialType) {
        String email = (String) claim.get("email");
        if (StringUtils.isBlank(email)) {
            log.warn("Failed to get email from idToken! type: {}, claim: {}", socialType, claim);
            throw new AuthException(ErrorType.FAILED_AUTHENTICATION, "Failed to get email from idToken");
        }
        return email;
    }
}
