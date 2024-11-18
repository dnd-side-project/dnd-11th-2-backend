package com.dnd.runus.presentation.v1.oauth;

import com.dnd.runus.application.oauth.OauthService;
import com.dnd.runus.global.exception.type.ApiErrorType;
import com.dnd.runus.global.exception.type.ErrorType;
import com.dnd.runus.presentation.annotation.MemberId;
import com.dnd.runus.presentation.v1.oauth.dto.request.SignInRequest;
import com.dnd.runus.presentation.v1.oauth.dto.request.SignUpRequest;
import com.dnd.runus.presentation.v1.oauth.dto.request.WithdrawRequest;
import com.dnd.runus.presentation.v1.oauth.dto.response.SignResponse;
import com.dnd.runus.presentation.v1.oauth.dto.response.WithdrawResponse;
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

@Tag(name = "OAuth")
@RequestMapping("/api/v1/auth/oauth")
@RestController
@RequiredArgsConstructor
public class OauthController {

    private final OauthService oauthService;

    @Operation(
            summary = "로그인",
            description =
                    """
                    ## 로그인 API 입니다.<br>
                    인증토큰(idToken)만 존재할 경우 해당 api를 호출합니다.<br>
                    인증토큰(idToken)을 이용해 사용자를 인증합니다.<br>
                    해당 유저가 회원가입이 안되었을 경우, `404 USER_NOT_FOUND`(statusCode:404, code:OAUTH_001) 에러를 발생합니다.
                    """)
    @ApiErrorType({
        ErrorType.UNSUPPORTED_SOCIAL_TYPE,
        ErrorType.SOCIAL_MEMBER_NOT_FOUND,
        ErrorType.FAILED_AUTHENTICATION,
        ErrorType.UNSUPPORTED_JWT_TOKEN
    })
    @PostMapping("/sign-in")
    @ResponseStatus(HttpStatus.OK)
    public SignResponse signIn(@Valid @RequestBody SignInRequest request) {
        return oauthService.signIn(request);
    }

    @Operation(
            summary = "회원가입",
            description =
                    """
                ## 회원가입 API입니다.
                인증토큰(idToken), 사용자 정보가 존재할 경우 해당 API를 호출합니다.<br>
                인증토큰(idToken)과 사용자 정보로 사용자 등록을 합니다.<br>
                사용자의 닉네임은 처음 등록한 이름으로 저장 됩니다.
                """)
    @ApiErrorType({ErrorType.UNSUPPORTED_SOCIAL_TYPE, ErrorType.MALFORMED_ACCESS_TOKEN})
    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.OK)
    public SignResponse signUp(@Valid @RequestBody SignUpRequest request) {
        return oauthService.signUp(request);
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
    @ApiErrorType({ErrorType.UNSUPPORTED_SOCIAL_TYPE, ErrorType.FAILED_AUTHENTICATION})
    @PostMapping("/withdraw")
    @ResponseStatus(HttpStatus.OK)
    public WithdrawResponse withdraw(@MemberId long memberId, @Valid @RequestBody WithdrawRequest request) {
        boolean isSuccess = oauthService.revokeOauth(memberId, request);

        return new WithdrawResponse(isSuccess);
    }
}
