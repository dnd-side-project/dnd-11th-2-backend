package com.dnd.runus.domain.oauth.service;

import com.dnd.runus.auth.exception.AuthException;
import com.dnd.runus.auth.oidc.provider.OidcProviderFactory;
import com.dnd.runus.auth.token.TokenProviderModule;
import com.dnd.runus.auth.token.dto.AuthTokenDto;
import com.dnd.runus.domain.badge.BadgeAchievementRepository;
import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.member.MemberLevelRepository;
import com.dnd.runus.domain.member.MemberRepository;
import com.dnd.runus.domain.member.SocialProfile;
import com.dnd.runus.domain.member.SocialProfileRepository;
import com.dnd.runus.domain.oauth.dto.request.OauthRequest;
import com.dnd.runus.domain.oauth.dto.request.WithdrawRequest;
import com.dnd.runus.domain.oauth.dto.response.TokenResponse;
import com.dnd.runus.domain.running.RunningRecordRepository;
import com.dnd.runus.global.constant.MemberRole;
import com.dnd.runus.global.constant.SocialType;
import com.dnd.runus.global.exception.NotFoundException;
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
    private final BadgeAchievementRepository badgeAchievementRepository;
    private final RunningRecordRepository runningRecordRepository;
    private final MemberLevelRepository memberLevelRepository;

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

        AuthTokenDto tokenDto = tokenProviderModule.generate(
                String.valueOf(socialProfile.member().memberId()));

        return TokenResponse.from(tokenDto);
    }

    @Transactional
    public void withdraw(long memberId, WithdrawRequest request) {

        memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException("존재하지 않은 사용자"));

        // 토큰 검증 -> 애플 지침
        Claims claim = oidcProviderFactory.getClaims(request.socialType(), request.idToken());
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
        String accessToken = oidcProviderFactory.getAccessToken(request.socialType(), request.authorizationCode());

        oidcProviderFactory.revoke(request.socialType(), accessToken);

        deleteMember(memberId);
    }

    private SocialProfile createMember(String oauthId, String email, SocialType socialType, String nickname) {
        Member member = memberRepository.save(new Member(MemberRole.USER, nickname));

        return socialProfileRepository.save(SocialProfile.builder()
                .member(member)
                .socialType(socialType)
                .oauthId(oauthId)
                .oauthEmail(email)
                .build());
    }

    private void deleteMember(long memberId) {
        badgeAchievementRepository.deleteByMemberId(memberId);
        runningRecordRepository.deleteByMemberId(memberId);
        memberLevelRepository.deleteByMemberId(memberId);
        socialProfileRepository.deleteByMemberId(memberId);
        memberRepository.deleteById(memberId);
    }
}
