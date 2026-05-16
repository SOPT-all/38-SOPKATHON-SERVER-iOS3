package org.sopt.global.storage.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "업로드 완료 응답")
public record CompleteUploadResponse(
        @Schema(description = "S3 오브젝트 키", example = "images/2026/05/17/abc123.jpg")
        String objectKey,
        @Schema(description = "업로드된 파일의 공개 URL", example = "https://example.com/images/2026/05/17/abc123.jpg")
        String publicUrl
) {
}
