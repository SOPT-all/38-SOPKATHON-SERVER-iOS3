package org.sopt.domain.user.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.global.code.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

    USER_NOT_FOUND("USR-E001", HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다.");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;
}
