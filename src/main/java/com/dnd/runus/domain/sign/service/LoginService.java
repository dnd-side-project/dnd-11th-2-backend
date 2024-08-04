package com.dnd.runus.domain.sign.service;

import com.dnd.runus.auth.oidc.provider.OidcProviderFactory;
import com.dnd.runus.auth.token.TokenProviderModule;
import com.dnd.runus.auth.token.dto.AuthTokenDto;
import com.dnd.runus.domain.member.entity.Member;
import com.dnd.runus.domain.member.entity.PersonalProfile;
import com.dnd.runus.domain.member.entity.SocialProfile;
import com.dnd.runus.domain.member.repository.MemberRepository;
import com.dnd.runus.domain.member.repository.SocialProfileRepository;
import com.dnd.runus.domain.sign.dto.request.LoginRequest;
import com.dnd.runus.domain.sign.dto.response.TokenResponse;
import com.dnd.runus.global.constant.MemberRole;
import com.dnd.runus.global.constant.SocialType;
import com.dnd.runus.global.exception.BusinessException;
import com.dnd.runus.global.exception.type.ErrorType;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginService {

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
    public TokenResponse login(LoginRequest request) {

        Claims claim = oidcProviderFactory.getClaims(request.socialType(), request.idToken());
        String oAuthId = claim.getSubject();
        String email = String.valueOf(claim.get("email"));

        // 회원 가입 안되있으면 회원가입 진행
        if (!isLogin(request.socialType(), oAuthId)) {
            crateMember(oAuthId, email, request.socialType(), request.nickName());
        }

        SocialProfile socialProfile = socialProfileRepository
                .findBySocialTypeAndOauthId(request.socialType(), oAuthId)
                .orElseThrow(IllegalStateException::new);

        // 이메일 변경(사용자가 애플의 이메일을 변경한 후 로그인하면 해당 이메일 변경해줘야함. -> 리젝 사유 될 수 있음)
        if (!email.equals(socialProfile.getOauthEmail())) {
            socialProfile.updateEmail(email);
        }

        AuthTokenDto tokenDto = tokenProviderModule.generate(String.valueOf(socialProfile.getMemberId()));

        return TokenResponse.from(tokenDto);
    }

    private boolean isLogin(SocialType socialType, String authId) {
        return socialProfileRepository.existsBySocialTypeAndOauthId(socialType, authId);
    }

    private void crateMember(String authId, String email, SocialType socialType, String nickName) {
        if (socialProfileRepository.existsByOauthEmail(email)) {
            throw new BusinessException(ErrorType.VIOLATION_OCCURRED, "이미 존재하는 이메일");
        }

        // todo 체중 디폴트는 온보딩으로
        // 현재는 들어갈 때 임시로 70이 들어가도록 하드 코딩해둠
        Long memberId = memberRepository
                .save(Member.of(
                        MemberRole.USER,
                        nickName,
                        PersonalProfile.builder().weightKg(70).build()))
                .getId();

        socialProfileRepository.save(SocialProfile.of(socialType, authId, email, memberId));
    }
}
