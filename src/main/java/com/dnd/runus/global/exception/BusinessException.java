package com.dnd.runus.global.exception;

import com.dnd.runus.global.exception.type.ErrorType;

public class BusinessException extends BaseException {
    public BusinessException(ErrorType type, String message) {
        super(type, message);
    }

    public BusinessException(ErrorType type) {
        super(type, "No message");
    }
}
