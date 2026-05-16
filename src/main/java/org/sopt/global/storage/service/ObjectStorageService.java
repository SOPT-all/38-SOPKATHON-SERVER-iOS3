package org.sopt.global.storage.service;

import java.io.IOException;
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
import org.sopt.global.storage.dto.request.CompleteUploadRequest;
import org.sopt.global.storage.dto.request.PresignedUploadUrlRequest;
import org.sopt.global.storage.dto.response.CompleteUploadResponse;
import org.sopt.global.storage.dto.response.PresignedDownloadUrlResponse;
import org.sopt.global.storage.dto.response.PresignedUploadUrlResponse;
import org.sopt.global.storage.exception.StorageErrorCode;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
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
        String objectKey = generateObjectKey(normalizedContentType);
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(properties.bucket())
                .key(objectKey)
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

    public CompleteUploadResponse completeUpload(CompleteUploadRequest request) {
        validateObjectKey(request.objectKey());
        validateUploadMetadata(request.contentType(), request.contentLength());

        try {
            try (ResponseInputStream<GetObjectResponse> objectStream = s3Client.getObject(GetObjectRequest.builder()
                    .bucket(properties.bucket())
                    .key(request.objectKey())
                    .range("bytes=0-0")
                    .build())) {
                GetObjectResponse response = objectStream.response();
                validateUploadedObject(request, response);
            }
            return new CompleteUploadResponse(
                    request.objectKey(),
                    normalizeContentType(request.contentType()),
                    request.contentLength()
            );
        } catch (BaseException exception) {
            deleteObjectQuietly(request.objectKey());
            throw exception;
        } catch (IOException exception) {
            log.error("Failed to close uploaded object validation stream. bucket={}, key={}", properties.bucket(), request.objectKey(), exception);
            throw new BaseException(StorageErrorCode.OBJECT_STORAGE_REQUEST_FAILED);
        } catch (S3Exception exception) {
            log.error("Failed to validate uploaded object. bucket={}, key={}", properties.bucket(), request.objectKey(), exception);
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
        validateUploadMetadata(request.contentType(), request.contentLength());
    }

    private void validateUploadMetadata(String contentType, long contentLength) {
        String normalizedContentType = normalizeContentType(contentType);
        Set<String> allowedContentTypes = Set.copyOf(properties.allowedContentTypes());
        if (!allowedContentTypes.contains(normalizedContentType)) {
            throw new BaseException(StorageErrorCode.UNSUPPORTED_CONTENT_TYPE);
        }
        if (contentLength > properties.maxFileSize()) {
            throw new BaseException(StorageErrorCode.FILE_SIZE_EXCEEDED);
        }
    }

    private void validateUploadedObject(CompleteUploadRequest request, GetObjectResponse response) {
        long uploadedContentLength = resolveUploadedContentLength(response);
        if (uploadedContentLength > properties.maxFileSize()) {
            throw new BaseException(StorageErrorCode.FILE_SIZE_EXCEEDED);
        }
        if (uploadedContentLength != request.contentLength()) {
            throw new BaseException(StorageErrorCode.INVALID_UPLOADED_OBJECT);
        }

        String requestedContentType = normalizeContentType(request.contentType());
        String uploadedContentType = normalizeContentType(response.contentType());
        if (!requestedContentType.equals(uploadedContentType)) {
            throw new BaseException(StorageErrorCode.INVALID_UPLOADED_OBJECT);
        }
    }

    private long resolveUploadedContentLength(GetObjectResponse response) {
        String contentRange = response.contentRange();
        if (contentRange != null && contentRange.contains("/")) {
            return Long.parseLong(contentRange.substring(contentRange.lastIndexOf('/') + 1));
        }
        return response.contentLength();
    }

    private String generateObjectKey(String contentType) {
        LocalDate today = LocalDate.now(SEOUL_ZONE_ID);
        String extension = EXTENSION_BY_CONTENT_TYPE.get(contentType);
        return "%s/%d/%02d/%02d/%s.%s".formatted(
                properties.keyPrefix(),
                today.getYear(),
                today.getMonthValue(),
                today.getDayOfMonth(),
                UUID.randomUUID(),
                extension
        );
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

    private void deleteObjectQuietly(String objectKey) {
        try {
            s3Client.deleteObject(builder -> builder
                    .bucket(properties.bucket())
                    .key(objectKey)
            );
        } catch (S3Exception exception) {
            log.warn("Failed to delete invalid uploaded object. bucket={}, key={}", properties.bucket(), objectKey, exception);
        }
    }
}
