package com.dnd.runus.presentation.v1.member;

import com.dnd.runus.application.member.MemberService;
import com.dnd.runus.presentation.annotation.MemberId;
import com.dnd.runus.presentation.v1.member.dto.response.MyProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/members/profiles")
public class MemberProfileController {
    private final MemberService memberService;

    @GetMapping("me")
    @ResponseStatus(HttpStatus.OK)
    public MyProfileResponse getMyProfile(@MemberId long memberId) {
        return memberService.getMyProfile(memberId);
    }
}
