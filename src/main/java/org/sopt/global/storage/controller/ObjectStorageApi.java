package org.sopt.global.storage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.global.response.CommonApiResponse;
import org.sopt.global.storage.dto.request.CompleteUploadRequest;
import org.sopt.global.storage.dto.request.DeleteObjectRequest;
import org.sopt.global.storage.dto.request.PresignedDownloadUrlRequest;
import org.sopt.global.storage.dto.request.PresignedUploadUrlRequest;
import org.sopt.global.storage.dto.response.CompleteUploadResponse;
import org.sopt.global.storage.dto.response.PresignedDownloadUrlResponse;
import org.sopt.global.storage.dto.response.PresignedUploadUrlResponse;
import org.sopt.global.storage.exception.FileSizeExceededException;
import org.sopt.global.storage.exception.InvalidObjectKeyException;
import org.sopt.global.storage.exception.InvalidUploadedObjectException;
import org.sopt.global.storage.exception.ObjectStorageRequestFailedException;
import org.sopt.global.storage.exception.UnsupportedContentTypeException;
import org.sopt.global.swagger.ApiExceptions;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Image", description = "이미지 업로드 API")
public interface ObjectStorageApi {

    @Operation(summary = "이미지 업로드 URL 발급")
    @ApiResponse(responseCode = "200", description = "업로드 URL 발급 성공")
    @ApiExceptions({
            UnsupportedContentTypeException.class,
            FileSizeExceededException.class,
            ObjectStorageRequestFailedException.class
    })
    ResponseEntity<CommonApiResponse<PresignedUploadUrlResponse>> generateUploadUrl(
            @RequestBody PresignedUploadUrlRequest request
    );

    @Operation(summary = "이미지 업로드 완료 검증")
    @ApiResponse(responseCode = "200", description = "업로드 완료 검증 성공")
    @ApiExceptions({
            InvalidObjectKeyException.class,
            UnsupportedContentTypeException.class,
            FileSizeExceededException.class,
            InvalidUploadedObjectException.class,
            ObjectStorageRequestFailedException.class
    })
    ResponseEntity<CommonApiResponse<CompleteUploadResponse>> completeUpload(
            @RequestBody CompleteUploadRequest request
    );

    @Operation(summary = "이미지 다운로드 URL 발급")
    @ApiResponse(responseCode = "200", description = "다운로드 URL 발급 성공")
    @ApiExceptions({
            InvalidObjectKeyException.class,
            ObjectStorageRequestFailedException.class
    })
    ResponseEntity<CommonApiResponse<PresignedDownloadUrlResponse>> generateDownloadUrl(
            @RequestBody PresignedDownloadUrlRequest request
    );

    @Operation(summary = "이미지 삭제")
    @ApiResponse(responseCode = "204", description = "이미지 삭제 성공", content = @Content)
    @ApiExceptions({
            InvalidObjectKeyException.class,
            ObjectStorageRequestFailedException.class
    })
    ResponseEntity<CommonApiResponse<Void>> deleteObject(
            @RequestBody DeleteObjectRequest request
    );
}
