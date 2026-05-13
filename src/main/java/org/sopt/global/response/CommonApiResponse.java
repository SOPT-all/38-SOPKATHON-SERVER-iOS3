package org.sopt.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import org.sopt.global.code.ErrorCode;
import org.sopt.global.code.SuccessCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "공통 API 응답")
public record CommonApiResponse<T>(
        @Schema(description = "응답 코드", example = "GLB-S001")
        String code,
        @Schema(description = "성공 여부", example = "true")
        boolean success,
        @Schema(description = "응답 메시지", example = "요청이 성공했습니다.")
        String message,
        @Schema(description = "응답 데이터")
        T data
) {

    public static <T> ResponseEntity<CommonApiResponse<T>> successResponse(SuccessCode successCode, T data) {
        if (successCode.getHttpStatus() == HttpStatus.NO_CONTENT) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity
                .status(successCode.getHttpStatus())
                .body(successBody(successCode, data));
    }

    public static <T> CommonApiResponse<T> successBody(SuccessCode successCode, T data) {
        return new CommonApiResponse<>(successCode.getCode(), true, successCode.getMessage(), data);
    }

    public static <T> CommonApiResponse<T> failureBody(ErrorCode errorCode) {
        return new CommonApiResponse<>(errorCode.getCode(), false, errorCode.getMessage(), null);
    }

    public static <T> CommonApiResponse<T> failureBody(ErrorCode errorCode, T data) {
        return new CommonApiResponse<>(errorCode.getCode(), false, errorCode.getMessage(), data);
    }
}
