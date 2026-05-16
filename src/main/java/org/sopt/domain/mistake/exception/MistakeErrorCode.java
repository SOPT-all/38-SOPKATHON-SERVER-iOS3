package org.sopt.domain.mistake.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.global.code.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MistakeErrorCode implements ErrorCode {

    MISTAKE_DUPLICATE("MST-E001", HttpStatus.CONFLICT, "오늘 이미 실수 카드를 작성했습니다."),
    MISTAKE_NOT_FOUND("MST-E002", HttpStatus.NOT_FOUND, "존재하지 않는 실수입니다."),
    MISTAKE_ACCESS_DENIED("MST-E003", HttpStatus.FORBIDDEN, "해당 실수에 대한 접근 권한이 없습니다.");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;
}
