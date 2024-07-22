package com.dnd.runus.global.exception;

import com.dnd.runus.global.exception.type.PersistenceErrorType;

public class NotFoundException extends BaseException {
    public NotFoundException(String message) {
        super(PersistenceErrorType.ENTITY_NOT_FOUND, message);
    }
}
