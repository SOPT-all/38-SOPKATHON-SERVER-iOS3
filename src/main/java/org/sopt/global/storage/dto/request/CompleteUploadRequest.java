package org.sopt.global.storage.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CompleteUploadRequest(
        @NotBlank(message = "객체 키는 필수입니다.")
        String objectKey,
        @NotBlank(message = "Content-Type은 필수입니다.")
        String contentType,
        @NotNull(message = "파일 크기는 필수입니다.")
        @Positive(message = "파일 크기는 0보다 커야 합니다.")
        Long contentLength
) {
}
