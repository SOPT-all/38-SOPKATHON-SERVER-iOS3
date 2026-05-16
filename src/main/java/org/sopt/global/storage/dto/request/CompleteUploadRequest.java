package org.sopt.global.storage.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "업로드 완료 요청")
public record CompleteUploadRequest(
        @Schema(description = "S3 오브젝트 키", example = "images/2026/05/17/abc123.jpg")
        @NotBlank(message = "객체 키는 필수입니다.")
        String objectKey,

        @Schema(description = "파일 Content-Type", example = "image/jpeg")
        @NotBlank(message = "Content-Type은 필수입니다.")
        String contentType,

        @Schema(description = "파일 크기 (바이트)", example = "204800")
        @NotNull(message = "파일 크기는 필수입니다.")
        @Positive(message = "파일 크기는 0보다 커야 합니다.")
        Long contentLength
) {
}
