package com.dnd.runus.auth.exception;

import com.dnd.runus.global.exception.type.ErrorType;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class AuthException extends AuthenticationException {
    private final ErrorType type;
    private final String message;

    public AuthException(ErrorType type, String message) {
        super(message);
        this.type = type;
        this.message = message;
    }

    @Override
    public String toString() {
        return "AUTH 에러 타입: " + type + ", 사유: " + message;
    }
}
