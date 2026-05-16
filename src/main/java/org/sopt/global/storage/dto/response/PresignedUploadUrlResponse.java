package org.sopt.global.storage.dto.response;

import java.util.Map;

public record PresignedUploadUrlResponse(
        String objectKey,
        String uploadUrl,
        String method,
        long expiresInSeconds,
        Map<String, String> headers
) {
}
