package com.dnd.runus.presentation.v1.oauth;

import com.dnd.runus.application.oauth.OauthService;
import com.dnd.runus.global.exception.type.ApiErrorType;
import com.dnd.runus.global.exception.type.ErrorType;
import com.dnd.runus.presentation.annotation.MemberId;
import com.dnd.runus.presentation.v1.oauth.dto.request.OauthRequest;
import com.dnd.runus.presentation.v1.oauth.dto.request.WithdrawRequest;
import com.dnd.runus.presentation.v1.oauth.dto.response.TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "회원가입, 로그인, 회원탈퇴")
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
    @ResponseStatus(HttpStatus.OK)
    public TokenResponse signIn(@Valid @RequestBody OauthRequest request) {
        return oauthService.signIn(request);
    }

    @Operation(
            summary = "회원 탈퇴",
            description =
                    """
            ## 회원 탈퇴 API입니다.
            idToken과 authorizationCode로 애플에서 토큰을 발급합니다.<br>
            발급 받은 토큰으로 사용자의 서비스 해제 요청을 합니다.<br>
            사용자의 모든 데이터를 삭제합니다.
            """)
    @ApiErrorType({
        ErrorType.UNSUPPORTED_SOCIAL_TYPE,
        ErrorType.UNSUPPORTED_JWT_TOKEN,
        ErrorType.FAILED_AUTHENTICATION,
        ErrorType.MALFORMED_ACCESS_TOKEN,
        ErrorType.EXPIRED_ACCESS_TOKEN,
        ErrorType.UNHANDLED_EXCEPTION,
    })
    @PostMapping("/withdraw")
    @ResponseStatus(HttpStatus.OK)
    public void withdraw(@MemberId long memberId, @Valid @RequestBody WithdrawRequest request) {
        oauthService.withdraw(memberId, request);
    }
}
