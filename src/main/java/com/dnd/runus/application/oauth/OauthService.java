package com.dnd.runus.application.oauth;

import com.dnd.runus.auth.exception.AuthException;
import com.dnd.runus.auth.oidc.provider.OidcProvider;
import com.dnd.runus.auth.oidc.provider.OidcProviderRegistry;
import com.dnd.runus.auth.token.TokenProviderModule;
import com.dnd.runus.auth.token.dto.AuthTokenDto;
import com.dnd.runus.domain.badge.BadgeAchievementRepository;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievementPercentageRepository;
import com.dnd.runus.domain.challenge.achievement.ChallengeAchievementRepository;
import com.dnd.runus.domain.goalAchievement.GoalAchievementRepository;
import com.dnd.runus.domain.member.*;
import com.dnd.runus.domain.running.RunningRecord;
import com.dnd.runus.domain.running.RunningRecordRepository;
import com.dnd.runus.domain.scale.ScaleAchievementRepository;
import com.dnd.runus.global.constant.MemberRole;
import com.dnd.runus.global.constant.SocialType;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OauthService {

    private final OidcProviderRegistry oidcProviderRegistry;
    private final TokenProviderModule tokenProviderModule;

    private final MemberRepository memberRepository;
    private final SocialProfileRepository socialProfileRepository;
    private final MemberLevelRepository memberLevelRepository;
    private final BadgeAchievementRepository badgeAchievementRepository;
    private final RunningRecordRepository runningRecordRepository;
    private final ChallengeAchievementRepository challengeAchievementRepository;
    private final GoalAchievementRepository goalAchievementRepository;
    private final ChallengeAchievementPercentageRepository challengeAchievementPercentageRepository;
    private final ScaleAchievementRepository scaleAchievementRepository;

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
    public void revokeOauth(long memberId, WithdrawRequest request) {

        memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(Member.class, memberId));

        OidcProvider oidcProvider = oidcProviderRegistry.getOidcProviderBy(request.socialType());

        Claims claim = oidcProvider.getClaimsBy(request.idToken());
        String oauthId = claim.getSubject();

        SocialProfile socialProfile = socialProfileRepository
                .findBySocialTypeAndOauthId(request.socialType(), oauthId)
                .orElseThrow(() -> {
                    log.error(
                            "data conflict: 존재하지 않은 SocialProfile입니다. socialType: {}, oauthId: {}",
                            request.socialType(),
                            oauthId);
                    return new NotFoundException("존재하지 않은 SocialProfile");
                });

        if (memberId != socialProfile.member().memberId()) {
            log.error(
                    "DATA CONFLICT: MemberId and MemberId find by SocialProfile oauthId do not match each other! memberId: {}, socialProfileId: {}",
                    memberId,
                    socialProfile.socialProfileId());
            throw new NotFoundException("잘못된 데이터: Member and SocialProfile do not match each other");
        }

        // 탈퇴를 위한 access token 발급
        String accessToken = oidcProvider.getAccessToken(request.authorizationCode());
        oidcProvider.revoke(accessToken);
    }

    @Transactional
    public void deleteAllDataAboutMember(long memberId) {
        Member member =
                memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(Member.class, memberId));

        memberLevelRepository.deleteByMemberId(member.memberId());
        badgeAchievementRepository.deleteByMemberId(member.memberId());
        scaleAchievementRepository.deleteByMemberId(member.memberId());
        socialProfileRepository.deleteByMemberId(member.memberId());

        // running_record 조회
        List<RunningRecord> runningRecords = runningRecordRepository.findByMember(member);
        if (runningRecords.isEmpty()) {
            // running_record가 없으면 멤버 삭제 후 리턴
            memberRepository.deleteById(member.memberId());
            return;
        }

        // goal_achievement 삭제
        goalAchievementRepository.deleteByRunningRecords(runningRecords);

        // running_record로 challenge_achievement 조회
        List<Long> challengeAchievementIds = challengeAchievementRepository.findIdsByRunningRecords(runningRecords);
        // challenge_achievement가 존재하면 challenge_achievement_percentage, challenge_achievement 삭제
        if (!challengeAchievementIds.isEmpty()) {
            challengeAchievementPercentageRepository.deleteByChallengeAchievementIds(challengeAchievementIds);
            challengeAchievementRepository.deleteByIds(challengeAchievementIds);
        }

        // running_record 삭제
        runningRecordRepository.deleteByMemberId(member.memberId());

        memberRepository.deleteById(member.memberId());
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
