package com.dnd.runus.presentation.v1.oauth;

import com.dnd.runus.application.oauth.OauthService;
import com.dnd.runus.global.exception.type.ApiErrorType;
import com.dnd.runus.global.exception.type.ErrorType;
import com.dnd.runus.presentation.annotation.MemberId;
import com.dnd.runus.presentation.v1.oauth.dto.request.OauthRequest;
import com.dnd.runus.presentation.v1.oauth.dto.request.WithdrawRequest;
import com.dnd.runus.presentation.v1.oauth.dto.response.TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/auth/oauth")
@RestController
@RequiredArgsConstructor
public class OauthController {

    private final OauthService oauthService;

    @Operation(
            summary = "회원가입 및 로그인",
            description = "인증토큰(idToken)과 사용자 정보로 사용자 등록을 합니다."
                    + " 이메일, 사용자 이름에 대해 변경사항 있는 경우 알려 주세요!(OauthRequest 스키마를 참고해 주세요.)")
    @ApiErrorType({ErrorType.UNSUPPORTED_SOCIAL_TYPE, ErrorType.MALFORMED_ACCESS_TOKEN, ErrorType.UNSUPPORTED_JWT_TOKEN
    })
    @PostMapping
    public TokenResponse signIn(@Valid @RequestBody OauthRequest request) {
        return oauthService.signIn(request);
    }

    @PostMapping("/withdraw")
    public void withdraw(@MemberId long memberId, @Valid @RequestBody WithdrawRequest request) {
        oauthService.withdraw(memberId, request);
    }
}
