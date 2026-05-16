package org.sopt.global.storage.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.global.exception.BaseException;
import org.sopt.global.storage.config.ObjectStorageProperties;
import org.sopt.global.storage.dto.request.PresignedUploadUrlRequest;
import org.sopt.global.storage.dto.response.PresignedDownloadUrlResponse;
import org.sopt.global.storage.dto.response.PresignedUploadUrlResponse;
import org.sopt.global.storage.exception.StorageErrorCode;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class ObjectStorageService {

    private static final ZoneId SEOUL_ZONE_ID = ZoneId.of("Asia/Seoul");
    private static final Map<String, String> EXTENSION_BY_CONTENT_TYPE = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/heic", "heic",
            "image/webp", "webp"
    );

    private final ObjectStorageProperties properties;
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    public PresignedUploadUrlResponse generateUploadUrl(PresignedUploadUrlRequest request) {
        validateUploadRequest(request);

        String normalizedContentType = normalizeContentType(request.contentType());
        String objectKey = generateObjectKey(request.originalFileName(), normalizedContentType);
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(properties.bucket())
                .key(objectKey)
                .contentType(normalizedContentType)
                .build();
        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(properties.uploadUrlExpiration())
                .putObjectRequest(putObjectRequest)
                .build();

        try {
            PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
            return new PresignedUploadUrlResponse(
                    objectKey,
                    presignedRequest.url().toString(),
                    "PUT",
                    properties.uploadUrlExpiration().toSeconds(),
                    Map.of("Content-Type", normalizedContentType)
            );
        } catch (S3Exception exception) {
            log.error("Failed to generate presigned upload URL. bucket={}, key={}", properties.bucket(), objectKey, exception);
            throw new BaseException(StorageErrorCode.OBJECT_STORAGE_REQUEST_FAILED);
        }
    }

    public PresignedDownloadUrlResponse generateDownloadUrl(String objectKey) {
        validateObjectKey(objectKey);

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(properties.bucket())
                .key(objectKey)
                .build();
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(properties.downloadUrlExpiration())
                .getObjectRequest(getObjectRequest)
                .build();

        try {
            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
            return new PresignedDownloadUrlResponse(
                    objectKey,
                    presignedRequest.url().toString(),
                    "GET",
                    properties.downloadUrlExpiration().toSeconds()
            );
        } catch (S3Exception exception) {
            log.error("Failed to generate presigned download URL. bucket={}, key={}", properties.bucket(), objectKey, exception);
            throw new BaseException(StorageErrorCode.OBJECT_STORAGE_REQUEST_FAILED);
        }
    }

    public void deleteObject(String objectKey) {
        validateObjectKey(objectKey);

        try {
            s3Client.deleteObject(builder -> builder
                    .bucket(properties.bucket())
                    .key(objectKey)
            );
        } catch (S3Exception exception) {
            log.error("Failed to delete object. bucket={}, key={}", properties.bucket(), objectKey, exception);
            throw new BaseException(StorageErrorCode.OBJECT_STORAGE_REQUEST_FAILED);
        }
    }

    private void validateUploadRequest(PresignedUploadUrlRequest request) {
        String normalizedContentType = normalizeContentType(request.contentType());
        Set<String> allowedContentTypes = Set.copyOf(properties.allowedContentTypes());
        if (!allowedContentTypes.contains(normalizedContentType)) {
            throw new BaseException(StorageErrorCode.UNSUPPORTED_CONTENT_TYPE);
        }
        if (request.contentLength() > properties.maxFileSize()) {
            throw new BaseException(StorageErrorCode.FILE_SIZE_EXCEEDED);
        }
    }

    private String generateObjectKey(String originalFileName, String contentType) {
        LocalDate today = LocalDate.now(SEOUL_ZONE_ID);
        String extension = resolveExtension(originalFileName, contentType);
        return "%s/%d/%02d/%02d/%s.%s".formatted(
                properties.keyPrefix(),
                today.getYear(),
                today.getMonthValue(),
                today.getDayOfMonth(),
                UUID.randomUUID(),
                extension
        );
    }

    private String resolveExtension(String originalFileName, String contentType) {
        String fileExtension = extractFileExtension(originalFileName);
        if (!fileExtension.isBlank()) {
            return fileExtension;
        }
        return EXTENSION_BY_CONTENT_TYPE.getOrDefault(contentType, "bin");
    }

    private String extractFileExtension(String originalFileName) {
        int lastDotIndex = originalFileName.lastIndexOf('.');
        if (lastDotIndex < 0 || lastDotIndex == originalFileName.length() - 1) {
            return "";
        }
        return originalFileName.substring(lastDotIndex + 1)
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]", "");
    }

    private String normalizeContentType(String contentType) {
        return contentType.toLowerCase(Locale.ROOT).trim();
    }

    private void validateObjectKey(String objectKey) {
        String keyPrefix = properties.keyPrefix() + "/";
        if (!objectKey.startsWith(keyPrefix) || objectKey.contains("..")) {
            throw new BaseException(StorageErrorCode.INVALID_OBJECT_KEY);
        }
    }
}
