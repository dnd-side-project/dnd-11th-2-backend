package com.dnd.runus.global.exception;

import com.dnd.runus.global.exception.type.ErrorType;

public class NotFoundException extends BaseException {
    public NotFoundException(String message) {
        super(ErrorType.ENTITY_NOT_FOUND, message);
    }
}
