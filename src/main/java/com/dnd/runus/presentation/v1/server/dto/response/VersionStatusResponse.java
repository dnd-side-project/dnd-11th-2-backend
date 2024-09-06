package com.dnd.runus.presentation.v1.server.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record VersionStatusResponse(
        @Schema(description = "업데이트 필요 여부")
        boolean updateRequired
) {
}
