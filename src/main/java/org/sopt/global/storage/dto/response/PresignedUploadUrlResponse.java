package org.sopt.global.storage.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;

@Schema(description = "Presigned 업로드 URL 응답")
public record PresignedUploadUrlResponse(
        @Schema(description = "S3 오브젝트 키", example = "images/2026/05/17/abc123.jpg")
        String objectKey,
        @Schema(description = "파일 업로드용 Presigned URL", example = "https://bucket.s3.amazonaws.com/images/2026/05/17/abc123.jpg?X-Amz-Signature=...")
        String uploadUrl,
        @Schema(description = "URL 만료까지 남은 시간 (초)", example = "300")
        long expiresInSeconds,
        @Schema(description = "업로드 시 포함해야 할 헤더 맵", example = "{\"Content-Type\": \"image/jpeg\"}")
        Map<String, String> headers
) {
}
