package org.sopt.global.storage.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.global.code.GlobalSuccessCode;
import org.sopt.global.response.CommonApiResponse;
import org.sopt.global.storage.dto.request.CompleteUploadRequest;
import org.sopt.global.storage.dto.request.DeleteObjectRequest;
import org.sopt.global.storage.dto.request.PresignedDownloadUrlRequest;
import org.sopt.global.storage.dto.request.PresignedUploadUrlRequest;
import org.sopt.global.storage.dto.response.CompleteUploadResponse;
import org.sopt.global.storage.dto.response.PresignedDownloadUrlResponse;
import org.sopt.global.storage.dto.response.PresignedUploadUrlResponse;
import org.sopt.global.storage.service.ObjectStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/images")
public class ObjectStorageController {

    private final ObjectStorageService objectStorageService;

    @PostMapping("/presigned-upload")
    public ResponseEntity<CommonApiResponse<PresignedUploadUrlResponse>> generateUploadUrl(
            @Valid @RequestBody PresignedUploadUrlRequest request
    ) {
        PresignedUploadUrlResponse response = objectStorageService.generateUploadUrl(request);
        return CommonApiResponse.successResponse(GlobalSuccessCode.OK, response);
    }

    @PostMapping("/complete")
    public ResponseEntity<CommonApiResponse<CompleteUploadResponse>> completeUpload(
            @Valid @RequestBody CompleteUploadRequest request
    ) {
        CompleteUploadResponse response = objectStorageService.completeUpload(request);
        return CommonApiResponse.successResponse(GlobalSuccessCode.OK, response);
    }

    @PostMapping("/presigned-download")
    public ResponseEntity<CommonApiResponse<PresignedDownloadUrlResponse>> generateDownloadUrl(
            @Valid @RequestBody PresignedDownloadUrlRequest request
    ) {
        PresignedDownloadUrlResponse response = objectStorageService.generateDownloadUrl(request.objectKey());
        return CommonApiResponse.successResponse(GlobalSuccessCode.OK, response);
    }

    @DeleteMapping
    public ResponseEntity<CommonApiResponse<Void>> deleteObject(
            @Valid @RequestBody DeleteObjectRequest request
    ) {
        objectStorageService.deleteObject(request.objectKey());
        return CommonApiResponse.successResponse(GlobalSuccessCode.NO_CONTENT, null);
    }
}
