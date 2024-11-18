package com.dnd.runus.application.oauth;

import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.member.MemberRepository;
import com.dnd.runus.domain.member.SocialProfile;
import com.dnd.runus.domain.member.SocialProfileRepository;
import com.dnd.runus.global.constant.MemberRole;
import com.dnd.runus.global.constant.SocialType;
import com.dnd.runus.global.exception.BusinessException;
import com.dnd.runus.global.exception.type.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SocialProfileService {

    private final SocialProfileRepository socialProfileRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public boolean isSocialMemberExists(SocialType socialType, String oauthId, long memberId) {
        return socialProfileRepository
                .findBySocialTypeAndOauthId(socialType, oauthId)
                .filter(profile -> profile.member().memberId() == memberId)
                .isPresent();
    }

    @Transactional
    public SocialProfile findOrThrow(SocialType socialType, String oauthId, String email) {
        SocialProfile socialProfile = socialProfileRepository
                .findBySocialTypeAndOauthId(socialType, oauthId)
                .orElseThrow(() ->
                        new BusinessException(ErrorType.SOCIAL_MEMBER_NOT_FOUND, socialType + ", oauthId: " + oauthId));

        updateEmailIfChanged(socialProfile, email);
        return socialProfile;
    }

    @Transactional
    public SocialProfile findOrCreate(SocialType socialType, String oauthId, String email, String nickname) {
        Member member = memberRepository.save(new Member(MemberRole.USER, nickname));

        SocialProfile socialProfile = socialProfileRepository
                .findBySocialTypeAndOauthId(socialType, oauthId)
                .orElseGet(() -> createSocialProfile(member, socialType, oauthId, email));

        updateEmailIfChanged(socialProfile, email);
        return socialProfile;
    }

    private SocialProfile createSocialProfile(Member member, SocialType socialType, String oauthId, String email) {
        return socialProfileRepository.save(SocialProfile.builder()
                .member(member)
                .socialType(socialType)
                .oauthId(oauthId)
                .oauthEmail(email)
                .build());
    }

    private void updateEmailIfChanged(SocialProfile socialProfile, String email) {
        if (!email.equals(socialProfile.oauthEmail())) {
            socialProfileRepository.updateOauthEmail(socialProfile.socialProfileId(), email);
        }
    }
}
