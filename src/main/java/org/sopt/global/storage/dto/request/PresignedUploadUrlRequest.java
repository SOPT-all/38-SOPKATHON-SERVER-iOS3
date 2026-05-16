package org.sopt.global.storage.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Presigned 업로드 URL 요청")
public record PresignedUploadUrlRequest(
        @Schema(description = "원본 파일명", example = "mistake_photo.jpg")
        @NotBlank(message = "파일명은 필수입니다.")
        String originalFileName,

        @Schema(description = "파일 Content-Type", example = "image/jpeg")
        @NotBlank(message = "Content-Type은 필수입니다.")
        String contentType,

        @Schema(description = "파일 크기 (바이트)", example = "204800")
        @NotNull(message = "파일 크기는 필수입니다.")
        @Positive(message = "파일 크기는 0보다 커야 합니다.")
        Long contentLength
) {
}
