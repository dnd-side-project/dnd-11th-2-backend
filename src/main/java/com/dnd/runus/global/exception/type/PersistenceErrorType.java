package com.dnd.runus.global.exception.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

import static lombok.AccessLevel.PACKAGE;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor(access = PACKAGE)
public enum PersistenceErrorType implements ErrorType {
    ENTITY_NOT_FOUND(NOT_FOUND, "해당 엔티티를 찾을 수 없습니다"),
    CONSTRAINT_VIOLATION(NOT_ACCEPTABLE, "제약 조건에 어긋나는 값은 저장할 수 없습니다"),
    ;
    private final HttpStatus httpStatus;
    private final String message;
}
