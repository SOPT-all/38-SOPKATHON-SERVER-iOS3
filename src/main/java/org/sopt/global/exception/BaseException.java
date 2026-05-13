package org.sopt.global.exception;

import lombok.Getter;
import org.sopt.global.code.ErrorCode;

@Getter
public class BaseException extends RuntimeException {

    private final ErrorCode errorCode;

    public BaseException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
