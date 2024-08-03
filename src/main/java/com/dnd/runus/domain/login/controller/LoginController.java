package com.dnd.runus.domain.login.controller;

import com.dnd.runus.domain.login.dto.request.LoginRequest;
import com.dnd.runus.domain.login.dto.response.TokenResponse;
import com.dnd.runus.domain.login.service.LoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/login")
@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/")
    public TokenResponse signUp(@Valid @RequestBody LoginRequest request) {
        return loginService.login(request);
    }
}
