package com.dnd.runus.application.oauth;

import com.dnd.runus.auth.exception.AuthException;
import com.dnd.runus.auth.oidc.provider.OidcProvider;
import com.dnd.runus.auth.oidc.provider.OidcProviderRegistry;
import com.dnd.runus.auth.token.TokenProviderModule;
import com.dnd.runus.auth.token.dto.AuthTokenDto;
import com.dnd.runus.domain.badge.BadgeAchievementRepository;
import com.dnd.runus.domain.member.*;
import com.dnd.runus.domain.running.RunningRecordRepository;
import com.dnd.runus.global.constant.MemberRole;
import com.dnd.runus.global.constant.SocialType;
import com.dnd.runus.global.exception.NotFoundException;
import com.dnd.runus.global.exception.type.ErrorType;
import com.dnd.runus.presentation.v1.oauth.dto.request.OauthRequest;
import com.dnd.runus.presentation.v1.oauth.dto.request.WithdrawRequest;
import com.dnd.runus.presentation.v1.oauth.dto.response.TokenResponse;
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

    private final OidcProviderRegistry oidcProviderRegistry;
    private final TokenProviderModule tokenProviderModule;

    private final MemberRepository memberRepository;
    private final SocialProfileRepository socialProfileRepository;
    private final BadgeAchievementRepository badgeAchievementRepository;
    private final RunningRecordRepository runningRecordRepository;
    private final MemberLevelRepository memberLevelRepository;

    /**
     * 회원가입 유무 확인 후 회원가입/로그인 진행
     *
     * @param request 로그인 request
     * @return TokenResponse
     */
    @Transactional
    public TokenResponse signIn(OauthRequest request) {
        OidcProvider oidcProvider = oidcProviderRegistry.getOidcProviderBy(request.socialType());

        Claims claim = oidcProvider.getClaimsBy(request.idToken());
        String oauthId = claim.getSubject();
        String email = String.valueOf(claim.get("email"));
        if (StringUtils.isBlank(email)) {
            log.warn("Failed to get email from idToken! type: {}, claim: {}", request.socialType(), claim);
            throw new AuthException(ErrorType.FAILED_AUTHENTICATION, "Failed to get email from idToken");
        }

        // 사용자가 존재하면 -> 이메일, 닉네임 update
        // 비회원일 경우 insert
        Long memberId = socialProfileRepository
                .findBySocialTypeAndOauthId(request.socialType(), oauthId)
                .map(socialProfile -> {
                    // 이메일, 닉네임 update
                    if (!email.equals(socialProfile.oauthEmail())) {
                        socialProfileRepository.updateOauthEmail(socialProfile.socialProfileId(), email);
                    }
                    memberRepository.updateNicknameById(socialProfile.member().memberId(), request.nickName());
                    return socialProfile.member().memberId();
                })
                .orElseGet(() -> createMember(oauthId, email, request.socialType(), request.nickName())
                        .member()
                        .memberId());

        AuthTokenDto tokenDto = tokenProviderModule.generate(String.valueOf(memberId));

        return TokenResponse.from(tokenDto);
    }

    @Transactional
    public void withdraw(long memberId, WithdrawRequest request) {

        memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(Member.class, memberId));
        OidcProvider oidcProvider = oidcProviderRegistry.getOidcProviderBy(request.socialType());

        Claims claim = oidcProvider.getClaimsBy(request.idToken());
        String oauthId = claim.getSubject();

        SocialProfile socialProfile = socialProfileRepository
                .findBySocialTypeAndOauthId(request.socialType(), oauthId)
                .orElseThrow(() -> new NotFoundException("존재하지 않은 SocialProfile"));

        if (memberId != socialProfile.member().memberId()) {
            log.error(
                    "MemberId and MemberId find by SocialProfile oauthId do not match each other! memberId: {}, socialProfileId: {}",
                    memberId,
                    socialProfile.socialProfileId());
            throw new NotFoundException("잘못된 데이터: Member and SocialProfile do not match each other");
        }

        // 탈퇴를 위한 access token 발급
        String accessToken = oidcProvider.getAccessToken(request.authorizationCode());
        oidcProvider.revoke(accessToken);

        deleteMember(memberId);
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

    private void deleteMember(long memberId) {
        badgeAchievementRepository.deleteByMemberId(memberId);
        // todo 챌린지 삭제(챌린지 기능 구현 완료 후)
        runningRecordRepository.deleteByMemberId(memberId);
        memberLevelRepository.deleteByMemberId(memberId);
        socialProfileRepository.deleteByMemberId(memberId);
        memberRepository.deleteById(memberId);
    }
}
