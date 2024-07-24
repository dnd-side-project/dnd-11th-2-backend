package com.dnd.runus.domain.example;

import com.dnd.runus.global.exception.BusinessException;
import com.dnd.runus.global.exception.type.ApiErrorType;
import com.dnd.runus.global.exception.type.ErrorType;
import io.swagger.v3.oas.annotations.Operation;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/examples")
public class ExampleController { // FIXME: 추후 삭제
    @Operation(summary = "페이징 테스트, 복잡한 페이징(pageable) 결과는 나중에 정해서 간략화해도 좋을 것 같아요")
    @GetMapping("/pagination")
    public Page<?> a(Pageable pageable) {
        List<String> list = List.of("a", "b", "c", "d", "e", "f", "g", "h", "i", "j");
        List<String> sublist = list.subList(
                (int) pageable.getOffset(), Math.min((int) pageable.getOffset() + pageable.getPageSize(), list.size()));
        return new PageImpl<>(sublist, pageable, list.size());
    }

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

    @Operation(summary = "데이터 테스트, 시간 관련 형식도 정하면 좋을 것 같아요")
    @GetMapping("/data")
    public MyData data() {
        return new MyData();
    }

    @Getter
    public static class MyData {
        int a;
        String b = "b";
        Instant c = Instant.now();
        String d = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
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
