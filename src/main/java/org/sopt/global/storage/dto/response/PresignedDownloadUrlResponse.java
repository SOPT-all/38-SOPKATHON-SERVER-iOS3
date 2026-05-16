package org.sopt.global.storage.dto.response;

public record PresignedDownloadUrlResponse(
        String objectKey,
        String downloadUrl,
        String method,
        long expiresInSeconds
) {
}
