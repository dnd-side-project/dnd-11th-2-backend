package com.dnd.runus.presentation.v2.member;

import com.dnd.runus.application.member.MemberService;
import com.dnd.runus.presentation.annotation.MemberId;
import com.dnd.runus.presentation.v1.member.dto.response.MyProfileResponse;
import com.dnd.runus.presentation.v2.member.dto.response.MyProfileResponseV2;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v2/members/profiles")
public class MemberProfileControllerV2 {
    private final MemberService memberService;

    @GetMapping("me")
    @ResponseStatus(HttpStatus.OK)
    public MyProfileResponseV2 getMyProfile(@MemberId long memberId) {
        MyProfileResponse myProfile = memberService.getMyProfile(memberId);

        // 퍼센테이지
        double leftNextLevelKm = myProfile.nextLevelEndExpMeter() - myProfile.currentExpMeter();
        double divisor = myProfile.nextLevelEndExpMeter() - myProfile.nextLevelStartExpMeter();
        double percentage = 1 - (leftNextLevelKm / divisor);

        return MyProfileResponseV2.of(myProfile, percentage);
    }
}
