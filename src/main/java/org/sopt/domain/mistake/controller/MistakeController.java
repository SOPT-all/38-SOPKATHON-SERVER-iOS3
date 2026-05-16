package org.sopt.domain.mistake.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.domain.mistake.dto.request.MistakeCreateRequest;
import org.sopt.domain.mistake.dto.response.MistakeDetailResponse;
import org.sopt.domain.mistake.service.MistakeService;
import org.sopt.global.code.GlobalSuccessCode;
import org.sopt.global.response.CommonApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/mistakes")
public class MistakeController implements MistakeApi {

    private final MistakeService mistakeService;

    @PostMapping
    public ResponseEntity<CommonApiResponse<Void>> create(
            @RequestHeader("User-Id") Long userId,
            @Valid @RequestBody MistakeCreateRequest request
    ) {
        mistakeService.create(userId, request);
        return CommonApiResponse.successResponse(GlobalSuccessCode.CREATED, null);
    }

    @GetMapping("/{mistakeId}")
    public ResponseEntity<CommonApiResponse<MistakeDetailResponse>> getDetail(
            @RequestHeader("User-Id") Long userId,
            @PathVariable Long mistakeId
    ) {
        return CommonApiResponse.successResponse(GlobalSuccessCode.OK, mistakeService.getDetail(userId, mistakeId));
    }
}
