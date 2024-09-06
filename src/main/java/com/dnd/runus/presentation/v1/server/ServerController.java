package com.dnd.runus.presentation.v1.server;

import com.dnd.runus.presentation.v1.server.dto.response.VersionStatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "서버")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/servers")
public class ServerController {

    @GetMapping("versions")
    @Operation(summary = "앱의 버전을 확인합니다.")
    public VersionStatusResponse checkVersion(@RequestParam String version) {
        // FIXME: 버전 체크 로직 구현 필요
        return new VersionStatusResponse(false);
    }
}
