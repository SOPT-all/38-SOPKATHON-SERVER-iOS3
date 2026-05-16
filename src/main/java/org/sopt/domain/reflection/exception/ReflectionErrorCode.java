package org.sopt.domain.reflection.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.global.code.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReflectionErrorCode implements ErrorCode {

    REFLECTION_INVALID_EMOJI_INDEX("RFL-E001", HttpStatus.BAD_REQUEST, "emojiIndex는 1, 2, 3, 4 중 하나여야 합니다."),
    REFLECTION_DUPLICATE("RFL-E002", HttpStatus.CONFLICT, "이미 해당 실수에 회고가 존재합니다.");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;
}
