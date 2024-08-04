package com.dnd.runus.domain.oauth.controller;

import com.dnd.runus.domain.oauth.dto.request.SignInRequest;
import com.dnd.runus.domain.oauth.dto.response.TokenResponse;
import com.dnd.runus.domain.oauth.service.OauthService;
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

    @PostMapping
    public TokenResponse SignIn(@Valid @RequestBody SignInRequest request) {
        return oauthService.SignIn(request);
    }
}
