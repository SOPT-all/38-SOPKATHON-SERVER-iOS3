package org.sopt.domain.reflection.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.domain.reflection.dto.request.ReflectionCreateRequest;
import org.sopt.domain.reflection.dto.response.ReflectionCreateResponse;
import org.sopt.domain.reflection.service.ReflectionService;
import org.sopt.global.code.GlobalSuccessCode;
import org.sopt.global.response.CommonApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/mistakes")
public class ReflectionController implements ReflectionApi {

    private final ReflectionService reflectionService;

    @PostMapping("/{mistakeId}/reflections")
    public ResponseEntity<CommonApiResponse<ReflectionCreateResponse>> create(
            @RequestHeader("User-Id") Long userId,
            @PathVariable Long mistakeId,
            @Valid @RequestBody ReflectionCreateRequest request
    ) {
        ReflectionCreateResponse response = reflectionService.create(userId, mistakeId, request);
        return CommonApiResponse.successResponse(GlobalSuccessCode.OK, response);
    }
}
