package com.dnd.runus.domain.example;

import com.dnd.runus.global.exception.BusinessException;
import com.dnd.runus.global.exception.type.ApiErrorType;
import com.dnd.runus.global.exception.type.ErrorType;
import io.swagger.v3.oas.annotations.Operation;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/examples")
public class ExampleController { // FIXME: 추후 삭제
    @Operation(summary = "input 테스트, input을 반환합니다.")
    @GetMapping("/input")
    public String name(@RequestParam String input) {
        return input;
    }

    @Operation(summary = "header 테스트, request와 함께 입력한 header들을 반환합니다.")
    @GetMapping("/headers")
    public Map<String, String> headers(@RequestHeader(value = "authorization") Map<String, String> headers) {
        return headers;
    }

    @Operation(summary = "시간, 날짜 데이터 테스트")
    @GetMapping("/data")
    public MyData data() {
        return new MyData();
    }

    @Getter
    public static class MyData {
        int number = 0;
        LocalTime time = LocalTime.now();
        LocalDate date = LocalDate.now();
        LocalDateTime dateTime = LocalDateTime.now();
    }

    @GetMapping("/empty")
    @ResponseStatus(HttpStatus.OK)
    public void empty() {}

    @Operation(summary = "COOKIE_NOT_FOND 에러 테스트")
    @ApiErrorType({ErrorType.COOKIE_NOT_FOND, ErrorType.FAILED_VALIDATION})
    @GetMapping("/errors")
    public String errors() {
        throw new BusinessException(ErrorType.COOKIE_NOT_FOND, "쿠키 이름~~");
    }
}
