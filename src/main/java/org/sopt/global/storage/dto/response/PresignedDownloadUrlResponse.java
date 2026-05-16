package org.sopt.global.storage.dto.response;

public record PresignedDownloadUrlResponse(
        String downloadUrl,
        long expiresInSeconds
) {
}
