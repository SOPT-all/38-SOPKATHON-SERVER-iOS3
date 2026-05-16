package org.sopt.global.storage.dto.response;

public record CompleteUploadResponse(
        String objectKey,
        String contentType,
        long contentLength,
        String publicUrl
) {
}
