package com.dnd.runus.domain.login.dto.request;

import com.dnd.runus.global.constant.SocialType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record LoginRequest(
    @NotNull
    SocialType socialType,
    @NotBlank
    String idToken,
    @NotBlank
    @Email
    String email,
    @NotBlank
    String nickName
){
}
