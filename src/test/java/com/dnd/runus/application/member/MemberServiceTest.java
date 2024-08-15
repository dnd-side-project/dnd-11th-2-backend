package com.dnd.runus.application.member;

import com.dnd.runus.domain.level.Level;
import com.dnd.runus.domain.member.MemberLevel;
import com.dnd.runus.domain.member.MemberLevelRepository;
import com.dnd.runus.presentation.v1.member.dto.response.MyProfileResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberLevelRepository memberLevelRepository;

    @DisplayName("내 프로필 조회 성공 - 현재 레벨 1, 경험치 500일때, 0km로 표현 (소수점 버림)")
    @Test
    void getMyProfile_givenCurrentLevel1AndExp500_thenSuccess() {
        // given
        long memberId = 1L;
        Level level = new Level(1, 0, 1000, "imageUrl");
        given(memberLevelRepository.findByMemberIdWithLevel(memberId)).willReturn(new MemberLevel.Current(level, 500));

        // when
        MyProfileResponse myProfileResponse = memberService.getMyProfile(memberId);

        // then
        assertEquals("imageUrl", myProfileResponse.profileImageUrl());
        assertEquals("0km", myProfileResponse.currentKm());
        assertEquals("Level 2", myProfileResponse.nextLevelName());
    }

    @DisplayName("내 프로필 조회 성공 - 현재 레벨 2, 경험치 1500일때, 1km로 표현 (소수점 버림)")
    @Test
    void getMyProfile_givenCurrentLevel1AndExp1500_thenSuccess() {
        // given
        long memberId = 1L;
        Level level = new Level(2, 1000, 2000, "imageUrl");
        given(memberLevelRepository.findByMemberIdWithLevel(memberId)).willReturn(new MemberLevel.Current(level, 1500));

        // when
        MyProfileResponse myProfileResponse = memberService.getMyProfile(memberId);

        // then
        assertEquals("imageUrl", myProfileResponse.profileImageUrl());
        assertEquals("1km", myProfileResponse.currentKm());
        assertEquals("Level 3", myProfileResponse.nextLevelName());
    }
}
