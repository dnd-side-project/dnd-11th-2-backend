package com.dnd.runus.global.exception.type;

import org.springframework.http.HttpStatus;

public interface ErrorType {
    String name();

    HttpStatus httpStatus();

    String message();
}
