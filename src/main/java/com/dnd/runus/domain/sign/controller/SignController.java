package com.dnd.runus.domain.sign.controller;

import com.dnd.runus.domain.sign.dto.request.LoginRequest;
import com.dnd.runus.domain.sign.dto.response.TokenResponse;
import com.dnd.runus.domain.sign.service.LoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/sign")
@RestController
@RequiredArgsConstructor
public class SignController {

    private final LoginService loginService;

    @PostMapping("/in")
    public TokenResponse signUp(@Valid @RequestBody LoginRequest request) {
        return loginService.login(request);
    }
}
