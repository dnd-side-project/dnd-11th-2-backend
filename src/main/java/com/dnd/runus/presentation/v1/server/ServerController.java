package com.dnd.runus.presentation.v1.server;

import com.dnd.runus.application.server.DeviceType;
import com.dnd.runus.application.server.ServerVersionService;
import com.dnd.runus.application.server.Version;
import com.dnd.runus.presentation.v1.server.dto.response.VersionStatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

    private final ServerVersionService serverVersionService;

    @GetMapping("versions")
    @Operation(summary = "앱 버전 지원 여부 확인", description = "기기의 앱 버전을 확인하고 앱 업데이트가 필요한지 여부를 반환합니다.")
    @Parameter(name = "version", description = "앱 버전", required = true, example = "1.0.0")
    public VersionStatusResponse checkVersion(@RequestParam String version) {
        boolean updateRequired = serverVersionService.isUpdateRequired(Version.parse(version), DeviceType.IOS);
        return new VersionStatusResponse(updateRequired);
    }
}
