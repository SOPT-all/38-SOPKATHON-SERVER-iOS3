package org.sopt.domain.mistake.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.global.code.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MistakeErrorCode implements ErrorCode {

    MISTAKE_DUPLICATE("MST-E001", HttpStatus.CONFLICT, "오늘 이미 실수 카드를 작성했습니다.");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;
}
