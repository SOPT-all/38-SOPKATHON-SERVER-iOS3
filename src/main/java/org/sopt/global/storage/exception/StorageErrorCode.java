package org.sopt.global.storage.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.global.code.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum StorageErrorCode implements ErrorCode {

    UNSUPPORTED_CONTENT_TYPE("STG-E001", HttpStatus.BAD_REQUEST, "지원하지 않는 이미지 형식입니다."),
    FILE_SIZE_EXCEEDED("STG-E002", HttpStatus.BAD_REQUEST, "이미지 파일 크기가 허용 범위를 초과했습니다."),
    INVALID_OBJECT_KEY("STG-E003", HttpStatus.BAD_REQUEST, "잘못된 이미지 객체 키입니다."),
    OBJECT_STORAGE_REQUEST_FAILED("STG-E004", HttpStatus.INTERNAL_SERVER_ERROR, "Object Storage 요청에 실패했습니다.");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;
}
