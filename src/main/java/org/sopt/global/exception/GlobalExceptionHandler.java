package org.sopt.global.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.sopt.global.code.ErrorCode;
import org.sopt.global.code.GlobalErrorCode;
import org.sopt.global.response.CommonApiResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 도메인에서 명시적으로 발생시킨 비즈니스 예외를 처리합니다.
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<CommonApiResponse<Void>> handleBaseException(BaseException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        log.warn("Business exception: {}", errorCode.getMessage());
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(CommonApiResponse.failureBody(errorCode));
    }

    // @Valid 요청 본문 검증 실패를 필드별 오류 메시지로 변환합니다.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonApiResponse<Map<String, Object>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception
    ) {
        Map<String, Object> errors = new LinkedHashMap<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return CommonApiResponse.failureBody(GlobalErrorCode.INVALID_REQUEST, errors);
    }

    // PathVariable, RequestParam 등 메서드 파라미터 검증 실패를 처리합니다.
    @ExceptionHandler(HandlerMethodValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonApiResponse<Void> handleHandlerMethodValidationException(
            HandlerMethodValidationException exception
    ) {
        log.warn("Method validation failed: {}", exception.getMessage());
        return CommonApiResponse.failureBody(GlobalErrorCode.INVALID_REQUEST);
    }

    // 잘못된 JSON 형식이나 읽을 수 없는 요청 본문을 처리합니다.
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonApiResponse<Void> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        log.warn("Request body is not readable: {}", exception.getMessage());
        return CommonApiResponse.failureBody(GlobalErrorCode.INVALID_REQUEST);
    }

    // 서비스 로직에서 발생한 잘못된 인자 예외를 처리합니다.
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonApiResponse<Void> handleIllegalArgumentException(IllegalArgumentException exception) {
        log.warn("Invalid argument: {}", exception.getMessage());
        return CommonApiResponse.failureBody(GlobalErrorCode.INVALID_REQUEST);
    }

    // DB 제약 조건 위반을 잘못된 요청으로 처리합니다.
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonApiResponse<Void> handleDataIntegrityViolationException(DataIntegrityViolationException exception) {
        log.warn("Database constraint violation: {}", exception.getMessage());
        return CommonApiResponse.failureBody(GlobalErrorCode.INVALID_REQUEST);
    }

    // JPA 또는 트랜잭션 시스템 오류를 서버 내부 오류로 처리합니다.
    @ExceptionHandler({
            JpaSystemException.class,
            TransactionSystemException.class
    })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonApiResponse<Void> handlePersistenceException(Exception exception) {
        log.error("Persistence system error occurred", exception);
        return CommonApiResponse.failureBody(GlobalErrorCode.INTERNAL_SERVER_ERROR);
    }

    // 존재하지 않는 정적 리소스 또는 경로 요청을 404로 처리합니다.
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CommonApiResponse<Void> handleNoResourceFoundException(NoResourceFoundException exception) {
        log.debug("Resource not found: {}", exception.getResourcePath());
        return CommonApiResponse.failureBody(GlobalErrorCode.RESOURCE_NOT_FOUND);
    }

    // 위에서 분류하지 못한 예외를 마지막 안전망으로 처리합니다.
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonApiResponse<Void> handleException(Exception exception) {
        log.error("Unexpected error occurred", exception);
        return CommonApiResponse.failureBody(GlobalErrorCode.INTERNAL_SERVER_ERROR);
    }
}
