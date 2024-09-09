package com.dnd.runus.presentation.v1.scale;

import com.dnd.runus.application.scale.ScaleService;
import com.dnd.runus.presentation.annotation.MemberId;
import com.dnd.runus.presentation.v1.scale.dto.ScaleCoursesResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "지구 한 바퀴")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/scale")
public class ScaleController {

    private final ScaleService scaleService;

    @GetMapping("courses")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "달성 기록 목록 조회",
            description =
                    """
                            달성한 코스 목록을 조회합니다. 달성한 코스 목록, 현재 진행중인 코스 정보를 반환합니다.<br>
                            - 달성한 코스가 없다면, 빈 리스트를 반환합니다.<br>
                            - 달성한 코스가 있다면, 달성한 코스 목록과 현재 진행중인 코스 정보를 반환합니다.
                            """)
    public ScaleCoursesResponse getCourses(@MemberId long memberId) {
        return scaleService.getAchievements(memberId);
    }
}
