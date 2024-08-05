package com.dnd.runus.domain.oauth.service;

import com.dnd.runus.auth.exception.AuthException;
import com.dnd.runus.auth.oidc.provider.OidcProviderFactory;
import com.dnd.runus.auth.token.TokenProviderModule;
import com.dnd.runus.auth.token.dto.AuthTokenDto;
import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.member.MemberRepository;
import com.dnd.runus.domain.member.SocialProfile;
import com.dnd.runus.domain.member.SocialProfileRepository;
import com.dnd.runus.domain.oauth.dto.request.OauthRequest;
import com.dnd.runus.domain.oauth.dto.response.TokenResponse;
import com.dnd.runus.global.constant.MemberRole;
import com.dnd.runus.global.constant.SocialType;
import com.dnd.runus.global.exception.type.ErrorType;
import io.jsonwebtoken.Claims;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OauthService {

    private final OidcProviderFactory oidcProviderFactory;
    private final TokenProviderModule tokenProviderModule;

    private final MemberRepository memberRepository;
    private final SocialProfileRepository socialProfileRepository;

    /**
     * 회원가입 유뮤 확인 후 회원가입/로그인 진행
     *
     * @param request 로그인 request
     * @return TokenResponse
     */
    @Transactional
    public TokenResponse signIn(OauthRequest request) {

        Claims claim = oidcProviderFactory.getClaims(request.socialType(), request.idToken());
        String oauthId = claim.getSubject();
        String email = String.valueOf(claim.get("email"));
        if (StringUtils.isBlank(email)) {
            log.warn("Failed to get email from idToken! type: {}, claim: {}", request.socialType(), claim);
            throw new AuthException(ErrorType.FAILED_AUTHENTICATION, "Failed to get email from idToken");
        }

        // 회원 가입 안되있으면 회원가입 진행
        SocialProfile socialProfile = socialProfileRepository
                .findBySocialTypeAndOauthId(request.socialType(), oauthId)
                .orElseGet(() -> createMember(oauthId, email, request.socialType(), request.nickName()));

        // 이메일 변경(사용자가 애플의 이메일을 변경한 후 로그인하면 해당 이메일 변경해줘야함. -> 리젝 사유 될 수 있음)
        if (!email.equals(socialProfile.oauthEmail())) {
            socialProfileRepository.updateOauthEmail(socialProfile.socialProfileId(), email);
        }

        AuthTokenDto tokenDto = tokenProviderModule.generate(String.valueOf(socialProfile.memberId()));

        return TokenResponse.from(tokenDto);
    }

    private SocialProfile createMember(String oauthId, String email, SocialType socialType, String nickname) {
        long memberId =
                memberRepository.save(new Member(MemberRole.USER, nickname)).memberId();

        return socialProfileRepository.save(SocialProfile.builder()
                .socialType(socialType)
                .oauthId(oauthId)
                .oauthEmail(email)
                .memberId(memberId)
                .build());
    }
}
